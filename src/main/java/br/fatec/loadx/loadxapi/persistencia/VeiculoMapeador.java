package br.fatec.loadx.loadxapi.persistencia;

import br.fatec.loadx.loadxapi.dominio.Caminhao;
import br.fatec.loadx.loadxapi.dominio.Carreta;
import br.fatec.loadx.loadxapi.dominio.Veiculo;

// ============================================================
// VeiculoMapeador.java  (NOVO)
// ------------------------------------------------------------
// É o "tradutor" entre dois mundos:
//   DOMÍNIO (Veiculo, Caminhao, Carreta) <-> BANCO (VeiculoRegistro)
//
// COMO FUNCIONA:
//   - paraRegistro: descobre se o veículo é Caminhao ou Carreta
//     (instanceof) e copia os dados para um VeiculoRegistro,
//     marcando a coluna "tipo".
//   - paraDominio: lê a coluna "tipo" e cria o objeto da classe
//     certa (new Caminhao ou new Carreta).
//
// É aqui que o polimorfismo e o banco se encontram: o resto do
// sistema só enxerga "Veiculo" e não precisa saber como ele é
// guardado.
//
// Os métodos são "static": podem ser chamados sem criar um objeto
// (VeiculoMapeador.paraRegistro(...)), como funções de um módulo VBA.
// ============================================================
public class VeiculoMapeador {

    public static final String TIPO_CAMINHAO = "CAMINHAO";
    public static final String TIPO_CARRETA = "CARRETA";

    // Impede criar objetos desta classe (só tem métodos utilitários).
    private VeiculoMapeador() {
    }

    // --------------------------------------------------------
    // DOMÍNIO -> REGISTRO (para gravar)
    // --------------------------------------------------------
    public static VeiculoRegistro paraRegistro(Veiculo veiculo) {
        VeiculoRegistro r = new VeiculoRegistro();
        r.setId(veiculo.getId());
        r.setDescricao(veiculo.getDescricao());
        r.setPbtc(veiculo.getPbtc());
        r.setNumeroEixos(veiculo.getNumeroEixos());
        r.setTara(veiculo.getTara());

        if (veiculo instanceof Caminhao c) {
            r.setTipo(TIPO_CAMINHAO);
            r.setCapacidadeLitros(c.getCapacidadeLitros());
        } else if (veiculo instanceof Carreta c) {
            r.setTipo(TIPO_CARRETA);
            r.setTaraSemirreboque(c.getTaraSemirreboque());
        } else {
            throw new IllegalArgumentException(
                    "Tipo de veículo não suportado: " + veiculo.getClass().getSimpleName());
        }
        return r;
    }

    // --------------------------------------------------------
    // REGISTRO -> DOMÍNIO (ao ler do banco)
    // --------------------------------------------------------
    public static Veiculo paraDominio(VeiculoRegistro r) {
        Veiculo veiculo;

        switch (r.getTipo()) {
            case TIPO_CAMINHAO -> veiculo = new Caminhao(
                    r.getDescricao(), r.getPbtc(), r.getNumeroEixos(),
                    r.getTara(), r.getCapacidadeLitros());
            case TIPO_CARRETA -> veiculo = new Carreta(
                    r.getDescricao(), r.getPbtc(), r.getNumeroEixos(),
                    r.getTara(), r.getTaraSemirreboque());
            default -> throw new IllegalArgumentException(
                    "Tipo de veículo desconhecido no banco: " + r.getTipo());
        }

        veiculo.setId(r.getId());
        return veiculo;
    }
}