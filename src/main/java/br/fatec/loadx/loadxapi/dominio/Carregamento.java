package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// Carregamento.java  (NOVO)
// ------------------------------------------------------------
// Junta um VEÍCULO e um PRODUTO para responder à pergunta central
// do LoadX: "quantos litros posso carregar com segurança?".
//
// Esta classe mostra COMPOSIÇÃO: um Carregamento "tem um"
// Veiculo e "tem um" Produto (diferente de herança, que é "é um").
//
// POLIMORFISMO em ação: a classe recebe um Veiculo e um Produto
// genéricos. Não sabe (nem precisa saber) se são Caminhao ou
// Carreta, ProdutoComum ou ProdutoComExpansao — cada objeto
// aplica a sua própria regra.
// ============================================================
public class Carregamento {

    private Veiculo veiculo;
    private Produto produto;
    private double capacidadeTanqueLitros;

    public Carregamento(Veiculo veiculo, Produto produto, double capacidadeTanqueLitros) {
        if (veiculo == null || produto == null) {
            throw new IllegalArgumentException("Veículo e produto são obrigatórios.");
        }
        if (capacidadeTanqueLitros <= 0) {
            throw new IllegalArgumentException("A capacidade do tanque deve ser maior que zero.");
        }
        this.veiculo = veiculo;
        this.produto = produto;
        this.capacidadeTanqueLitros = capacidadeTanqueLitros;
    }

    // Limite 1 — pelo VOLUME (regra do produto: folga ou expansão).
    public double limitePorVolume() {
        return produto.calcularVolumeMaximo(capacidadeTanqueLitros);
    }

    // Limite 2 — pelo PESO (regra do veículo: PBTC menos tara).
    // Converte o peso líquido permitido em litros usando a densidade.
    public double limitePorPeso() {
        return veiculo.calcularCapacidadeLiquida() / produto.getDensidade();
    }

    // O volume carregável é o MENOR dos dois limites: o que
    // estourar primeiro manda.
    public double calcularLitrosMaximos() {
        return Math.min(limitePorVolume(), limitePorPeso());
    }

    public double calcularPesoLiquido() {
        return produto.calcularPeso(calcularLitrosMaximos());
    }

    // Informa qual regra foi a limitante (útil para explicar o
    // resultado ao operador).
    public String fatorLimitante() {
        return limitePorVolume() <= limitePorPeso() ? "VOLUME (tanque)" : "PESO (PBTC)";
    }

    @Override
    public String toString() {
        return veiculo.getDescricao() + " carregando " + produto.getNome();
    }
}