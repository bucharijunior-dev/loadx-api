package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// Caminhao.java  (NOVO)
// ------------------------------------------------------------
// Um Caminhão é UM Veículo ("é-um"): essa é a ideia da HERANÇA.
// A palavra "extends" faz Caminhao receber, sem reescrever,
// tudo o que Veiculo tem: descrição, PBTC, eixos, tara,
// cargaCabe(), getters, setters etc.
//
// Em VBA isto NÃO é possível (VBA não tem herança). Aqui
// você ganha o reaproveitamento de código que o VBA não dá.
// ============================================================
public class Caminhao extends Veiculo {

    // Campo EXCLUSIVO do Caminhão (não existe em Veiculo).
    // A capacidade do tanque/carroceria, em litros, só faz
    // sentido aqui — por isso fica na filha, não na mãe.
    private double capacidadeLitros;

    // --------------------------------------------------------
    // CONSTRUTOR
    // "super(...)" chama o construtor da classe mãe, passando
    // os dados comuns. Depois a filha cuida do que é só dela.
    // --------------------------------------------------------
    public Caminhao(String descricao, double pbtc, int numeroEixos,
                    double tara, double capacidadeLitros) {
        super(descricao, pbtc, numeroEixos, tara);
        setCapacidadeLitros(capacidadeLitros);
    }

    // --------------------------------------------------------
    // IMPLEMENTAÇÃO DO MÉTODO ABSTRATO (POLIMORFISMO)
    // @Override avisa o compilador: "estou implementando/
    // substituindo um método da mãe". Se o nome ou os
    // parâmetros estiverem errados, ele reclama — uma proteção
    // útil contra erros de digitação.
    //
    // Regra do Caminhão (fictícia): a capacidade líquida é
    // simplesmente o que sobra do PBTC depois de descontar a tara.
    // --------------------------------------------------------
    @Override
    public double calcularCapacidadeLiquida() {
        return getPbtc() - getTara();
    }

    // --------------------------------------------------------
    // GETTER/SETTER do campo exclusivo, com validação
    // --------------------------------------------------------
    public double getCapacidadeLitros() { return capacidadeLitros; }

    public void setCapacidadeLitros(double capacidadeLitros) {
        // VERSÃO 2: aceita ZERO (capacidade não informada no cadastro).
        // A capacidade real é informada em cada programação/cálculo, que
        // exigem um valor maior que zero.
        if (capacidadeLitros < 0) {
            throw new IllegalArgumentException("A capacidade em litros não pode ser negativa.");
        }
        this.capacidadeLitros = capacidadeLitros;
    }

    // --------------------------------------------------------
    // toString() sobrescrito de novo: reaproveita o da mãe com
    // "super.toString()" e acrescenta o que é exclusivo da filha.
    // --------------------------------------------------------
    @Override
    public String toString() {
        return "[Caminhão] " + super.toString()
                + " | Capacidade: " + capacidadeLitros + " L";
    }
}
