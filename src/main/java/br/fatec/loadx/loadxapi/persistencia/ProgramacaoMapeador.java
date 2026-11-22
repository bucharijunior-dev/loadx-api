package br.fatec.loadx.loadxapi.persistencia;

import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.dominio.Programacao;
import br.fatec.loadx.loadxapi.dominio.Veiculo;

// ============================================================
// ProgramacaoMapeador.java  (NOVO)
// ------------------------------------------------------------
// Tradutor entre Programacao (domínio) e ProgramacaoRegistro (banco).
//
// DIFERENÇA em relação aos outros mapeadores:
//   - paraRegistro: guarda só os IDs do veículo e do produto.
//   - paraDominio: PRECISA RECEBER o Veiculo e o Produto já montados,
//     porque o registro só tem os ids. Quem busca esses dois objetos
//     nos repositórios e os entrega aqui é o ProgramacaoServico.
// ============================================================
public class ProgramacaoMapeador {

    // Impede criar objetos desta classe (só tem métodos utilitários).
    private ProgramacaoMapeador() {
    }

    // DOMÍNIO -> REGISTRO (para gravar)
    public static ProgramacaoRegistro paraRegistro(Programacao p) {
        ProgramacaoRegistro r = new ProgramacaoRegistro();
        r.setId(p.getId());
        r.setOrdemTransporte(p.getOrdemTransporte());
        r.setVeiculoId(p.getVeiculo().getId());
        r.setProdutoId(p.getProduto().getId());
        r.setCapacidadeTanqueLitros(p.getCapacidadeTanqueLitros());
        r.setDataHoraPrevista(p.getDataHoraPrevista());
        r.setStatus(p.getStatus());
        return r;
    }

    // REGISTRO -> DOMÍNIO (ao ler do banco)
    public static Programacao paraDominio(ProgramacaoRegistro r, Veiculo veiculo, Produto produto) {
        return Programacao.restaurar(
                r.getId(), r.getOrdemTransporte(), veiculo, produto,
                r.getCapacidadeTanqueLitros(), r.getDataHoraPrevista(), r.getStatus());
    }
}