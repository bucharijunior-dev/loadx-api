package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// ProdutoComExpansao.java  (NOVO)
// ------------------------------------------------------------
// Produto que se dilata com o calor e por isso exige uma
// reserva de espaço (margem de expansão) dentro do tanque.
// É o mesmo tipo de regra que o LoadX já trata para certos
// produtos químicos — aqui com valores fictícios.
//
// Mesma "mensagem" (calcularVolumeMaximo), resposta diferente
// de ProdutoComum: isso é POLIMORFISMO.
// ============================================================
public class ProdutoComExpansao extends Produto {

    // Campo EXCLUSIVO desta classe: percentual do tanque que
    // deve ficar vazio para absorver a dilatação (10 = 10%).
    private double margemExpansaoPercentual;

    public ProdutoComExpansao(String nome, double densidade,
                              double toleranciaPercentual,
                              double margemExpansaoPercentual) {
        super(nome, densidade, toleranciaPercentual);
        setMargemExpansaoPercentual(margemExpansaoPercentual);
    }

    // POLIMORFISMO: implementação do método abstrato — versão
    // que desconta a margem de expansão da capacidade do tanque.
    @Override
    public double calcularVolumeMaximo(double capacidadeTanqueLitros) {
        return capacidadeTanqueLitros * (1.0 - margemExpansaoPercentual / 100.0);
    }

    public double getMargemExpansaoPercentual() { return margemExpansaoPercentual; }

    public void setMargemExpansaoPercentual(double margemExpansaoPercentual) {
        if (margemExpansaoPercentual < 0 || margemExpansaoPercentual >= 100) {
            throw new IllegalArgumentException("A margem de expansão deve estar entre 0 e 100 (%).");
        }
        this.margemExpansaoPercentual = margemExpansaoPercentual;
    }

    @Override
    public String toString() {
        return "[Com expansão] " + super.toString()
                + " | Margem de expansão: " + margemExpansaoPercentual + "%";
    }
}