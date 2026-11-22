package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// ProdutoComum.java  (NOVO)
// ------------------------------------------------------------
// Produto que NÃO exige reserva especial de espaço no tanque.
// Usa a capacidade total do tanque (sem reserva de expansão).
//
// HERANÇA: "extends Produto" traz nome, densidade, tolerância,
// calcularPeso() e pesoDentroDaTolerancia() prontos.
// ============================================================
public class ProdutoComum extends Produto {

    // Produto comum pode ocupar 100% da capacidade do tanque.
    // (VERSÃO 2: antes era 0.98; foi alinhado ao LoadX em VBA, onde um
    // produto sem expansividade usa a capacidade bruta do tanque.)
    private static final double OCUPACAO_MAXIMA = 1.0;

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
