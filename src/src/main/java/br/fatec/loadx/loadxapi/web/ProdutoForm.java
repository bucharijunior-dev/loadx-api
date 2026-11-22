package br.fatec.loadx.loadxapi.web;

import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.dominio.ProdutoComExpansao;
import br.fatec.loadx.loadxapi.dominio.ProdutoComum;
import br.fatec.loadx.loadxapi.persistencia.ProdutoMapeador;

// ============================================================
// ProdutoForm.java  (NOVO)
// ------------------------------------------------------------
// Carrega os dados do FORMULÁRIO HTML de produto. Mesmo papel do
// VeiculoForm: o formulário é "plano" e o domínio tem ProdutoComum e
// ProdutoComExpansao. paraDominio() escolhe a classe certa; os setters
// do domínio validam (densidade > 0, tolerância entre 0 e 100...).
// ============================================================
public class ProdutoForm {

    private Long id;
    private String tipo;                         // "COMUM" ou "COM_EXPANSAO"
    private String nome;
    private Double densidade;
    private Double toleranciaPercentual;
    private Double margemExpansaoPercentual;     // só COM_EXPANSAO

    // FORMULÁRIO -> DOMÍNIO
    public Produto paraDominio() {
        exigir(tipo, "Tipo");
        Produto produto;

        switch (tipo) {
            case ProdutoMapeador.TIPO_COMUM -> produto = new ProdutoComum(
                    nome, exigir(densidade, "Densidade"),
                    exigir(toleranciaPercentual, "Tolerância (%)"));
            case ProdutoMapeador.TIPO_COM_EXPANSAO -> produto = new ProdutoComExpansao(
                    nome, exigir(densidade, "Densidade"),
                    exigir(toleranciaPercentual, "Tolerância (%)"),
                    exigir(margemExpansaoPercentual, "Margem de expansão (%)"));
            default -> throw new IllegalArgumentException("Tipo de produto inválido.");
        }

        produto.setId(id);
        return produto;
    }

    // DOMÍNIO -> FORMULÁRIO (para editar e para listar)
    public static ProdutoForm deDominio(Produto p) {
        ProdutoForm f = new ProdutoForm();
        f.id = p.getId();
        f.nome = p.getNome();
        f.densidade = p.getDensidade();
        f.toleranciaPercentual = p.getToleranciaPercentual();

        if (p instanceof ProdutoComExpansao pe) {
            f.tipo = ProdutoMapeador.TIPO_COM_EXPANSAO;
            f.margemExpansaoPercentual = pe.getMargemExpansaoPercentual();
        } else if (p instanceof ProdutoComum) {
            f.tipo = ProdutoMapeador.TIPO_COMUM;
        }
        return f;
    }

    private static <T> T exigir(T valor, String campo) {
        if (valor == null || (valor instanceof String s && s.isBlank())) {
            throw new IllegalArgumentException("Preencha o campo: " + campo + ".");
        }
        return valor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Double getDensidade() { return densidade; }
    public void setDensidade(Double densidade) { this.densidade = densidade; }

    public Double getToleranciaPercentual() { return toleranciaPercentual; }
    public void setToleranciaPercentual(Double toleranciaPercentual) { this.toleranciaPercentual = toleranciaPercentual; }

    public Double getMargemExpansaoPercentual() { return margemExpansaoPercentual; }
    public void setMargemExpansaoPercentual(Double margemExpansaoPercentual) { this.margemExpansaoPercentual = margemExpansaoPercentual; }
}
