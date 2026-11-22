package br.fatec.loadx.loadxapi.persistencia;

import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.dominio.ProdutoComExpansao;
import br.fatec.loadx.loadxapi.dominio.ProdutoComum;

// ============================================================
// ProdutoMapeador.java  (NOVO)
// ------------------------------------------------------------
// Tradutor entre o DOMÍNIO (Produto, ProdutoComum,
// ProdutoComExpansao) e o BANCO (ProdutoRegistro). Mesmo padrão
// do VeiculoMapeador:
//   - paraRegistro: descobre o tipo (instanceof) e copia os dados,
//     marcando a coluna "tipo".
//   - paraDominio: lê a coluna "tipo" e cria a classe certa.
// ============================================================
public class ProdutoMapeador {

    public static final String TIPO_COMUM = "COMUM";
    public static final String TIPO_COM_EXPANSAO = "COM_EXPANSAO";

    // Impede criar objetos desta classe (só tem métodos utilitários).
    private ProdutoMapeador() {
    }

    // --------------------------------------------------------
    // DOMÍNIO -> REGISTRO (para gravar)
    // --------------------------------------------------------
    public static ProdutoRegistro paraRegistro(Produto produto) {
        ProdutoRegistro r = new ProdutoRegistro();
        r.setId(produto.getId());
        r.setNome(produto.getNome());
        r.setDensidade(produto.getDensidade());
        r.setToleranciaPercentual(produto.getToleranciaPercentual());

        if (produto instanceof ProdutoComExpansao p) {
            r.setTipo(TIPO_COM_EXPANSAO);
            r.setMargemExpansaoPercentual(p.getMargemExpansaoPercentual());
        } else if (produto instanceof ProdutoComum) {
            r.setTipo(TIPO_COMUM);
        } else {
            throw new IllegalArgumentException(
                    "Tipo de produto não suportado: " + produto.getClass().getSimpleName());
        }
        return r;
    }

    // --------------------------------------------------------
    // REGISTRO -> DOMÍNIO (ao ler do banco)
    // --------------------------------------------------------
    public static Produto paraDominio(ProdutoRegistro r) {
        Produto produto;

        switch (r.getTipo()) {
            case TIPO_COMUM -> produto = new ProdutoComum(
                    r.getNome(), r.getDensidade(), r.getToleranciaPercentual());
            case TIPO_COM_EXPANSAO -> produto = new ProdutoComExpansao(
                    r.getNome(), r.getDensidade(), r.getToleranciaPercentual(),
                    r.getMargemExpansaoPercentual());
            default -> throw new IllegalArgumentException(
                    "Tipo de produto desconhecido no banco: " + r.getTipo());
        }

        produto.setId(r.getId());
        return produto;
    }
}