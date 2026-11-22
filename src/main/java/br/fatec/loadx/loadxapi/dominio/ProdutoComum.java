package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// ProdutoComum.java  (NOVO)
// ------------------------------------------------------------
// Produto que NÃO exige reserva especial de espaço no tanque.
// Usa apenas uma pequena folga de segurança fixa.
//
// HERANÇA: "extends Produto" traz nome, densidade, tolerância,
// calcularPeso() e pesoDentroDaTolerancia() prontos.
// ============================================================
public class ProdutoComum extends Produto {

    // Folga fixa de segurança (fictícia): o tanque é enchido
    // no máximo até 98% da capacidade.
    private static final double OCUPACAO_MAXIMA = 0.98;

    public ProdutoComum(String nome, double densidade, double toleranciaPercentual) {
        super(nome, densidade, toleranciaPercentual);
    }

    // POLIMORFISMO: implementação do método abstrato — versão simples.
    @Override
    public double calcularVolumeMaximo(double capacidadeTanqueLitros) {
        return capacidadeTanqueLitros * OCUPACAO_MAXIMA;
    }

    @Override
    public String toString() {
        return "[Comum] " + super.toString();
    }
}