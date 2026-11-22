package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// Carreta.java  (NOVO)
// ------------------------------------------------------------
// Uma Carreta (cavalo mecânico + semirreboque) também é UM
// Veículo, mas calcula a capacidade de outro jeito. É aqui que
// o POLIMORFISMO fica visível: duas classes respondem à MESMA
// mensagem (calcularCapacidadeLiquida) com comportamentos
// diferentes.
// ============================================================
public class Carreta extends Veiculo {

    // Campo EXCLUSIVO da Carreta: o peso do semirreboque, que
    // vem somado à tara do cavalo mecânico (a tara herdada).
    private double taraSemirreboque;

    public Carreta(String descricao, double pbtc, int numeroEixos,
                   double taraCavalo, double taraSemirreboque) {
        // A tara herdada de Veiculo guarda a do cavalo mecânico.
        super(descricao, pbtc, numeroEixos, taraCavalo);
        setTaraSemirreboque(taraSemirreboque);
    }

    // --------------------------------------------------------
    // IMPLEMENTAÇÃO DO MÉTODO ABSTRATO — versão da Carreta
    // Regra (fictícia): desconta a tara do cavalo E a do
    // semirreboque do PBTC. Compare com Caminhao: a assinatura
    // do método é igual, o corpo é diferente.
    // --------------------------------------------------------
    @Override
    public double calcularCapacidadeLiquida() {
        return getPbtc() - getTara() - taraSemirreboque;
    }

    public double getTaraSemirreboque() { return taraSemirreboque; }

    public void setTaraSemirreboque(double taraSemirreboque) {
        if (taraSemirreboque < 0) {
            throw new IllegalArgumentException("A tara do semirreboque não pode ser negativa.");
        }
        this.taraSemirreboque = taraSemirreboque;
    }

    @Override
    public String toString() {
        return "[Carreta] " + super.toString()
                + " | Tara semirreboque: " + taraSemirreboque + " kg";
    }
}