package br.fatec.loadx.loadxapi.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// ============================================================
// CalculoCarga.java  (NOVO)
// ------------------------------------------------------------
// MOTOR DE CÁLCULO do carregamento, equivalente à classe
// clsCarregamento do LoadX em VBA.
//
// IDEIA CENTRAL: o peso líquido da carga é o MENOR de três limites:
//   1) PESO ..... PBTC efetivo - tara
//   2) VOLUME ... capacidade máxima (com expansão do produto) x densidade
//   3) PEDIDO ... pedido do cliente x (1 + tolerância), ou o teto
//                 regulatório padrão quando não há pedido
// Os litros saem dividindo esse peso pela densidade.
//
// DIFERENÇA para a classe Carregamento: aqui a TARA é um dado de entrada
// (muda a cada viagem, como no VBA) e há DNIT, quebra-ondas, pedido e
// mínimo de litros. A classe Carregamento continua existindo, intacta,
// e é usada pelas programações.
//
// POLIMORFISMO: o limite de volume chama produto.calcularVolumeMaximo();
// cada tipo de produto aplica a sua própria regra de expansão, e esta
// classe não precisa saber qual é.
//
// Todos os dados usados no projeto são FICTÍCIOS.
// ============================================================
public class CalculoCarga {

    // Teto regulatório padrão quando não há pedido (como no VBA).
    public static final double TETO_SEM_PEDIDO_KG = 74000;
    // Acréscimo de PBTC com autorização DNIT.
    public static final double FATOR_DNIT = 1.05;

    private static final double EPS = 1e-6;
    private static final Locale BR = Locale.of("pt", "BR");

    private final Veiculo veiculo;
    private final Produto produto;
    private final double tara;
    private final double capacidadeTanqueLitros;
    private final double pedidoKg;                    // 0 = sem pedido
    private final double toleranciaPedidoPercentual;  // percentual "puro": 10 = 10%
    private final double minimoLitros;                // 0 = sem mínimo
    private final boolean dnit;
    private final boolean quebraOndas;

    // --------------------------------------------------------
    // CONSTRUTOR: valida as entradas (regra de negócio no domínio).
    // --------------------------------------------------------
    public CalculoCarga(Veiculo veiculo, Produto produto, double tara,
                        double capacidadeTanqueLitros, double pedidoKg,
                        double toleranciaPedidoPercentual, double minimoLitros,
                        boolean dnit, boolean quebraOndas) {
        if (veiculo == null || produto == null) {
            throw new IllegalArgumentException("Veículo e produto são obrigatórios.");
        }
        if (tara <= 0) {
            throw new IllegalArgumentException("A tara deve ser maior que zero.");
        }
        if (capacidadeTanqueLitros <= 0) {
            throw new IllegalArgumentException("A capacidade do tanque deve ser maior que zero.");
        }
        if (pedidoKg < 0 || minimoLitros < 0) {
            throw new IllegalArgumentException("Pedido e mínimo de litros não podem ser negativos.");
        }
        if (toleranciaPedidoPercentual < 0 || toleranciaPedidoPercentual > 100) {
            throw new IllegalArgumentException("A tolerância deve estar entre 0 e 100 (%).");
        }
        this.veiculo = veiculo;
        this.produto = produto;
        this.tara = tara;
        this.capacidadeTanqueLitros = capacidadeTanqueLitros;
        this.pedidoKg = pedidoKg;
        this.toleranciaPedidoPercentual = toleranciaPedidoPercentual;
        this.minimoLitros = minimoLitros;
        this.dnit = dnit;
        this.quebraOndas = quebraOndas;

        if (tara >= calcularPbtcEfetivo()) {
            throw new IllegalArgumentException("A tara não pode ser maior ou igual ao PBTC.");
        }
    }

    // --------------------------------------------------------
    // LIMITES
    // --------------------------------------------------------

    // PBTC efetivo: +5% quando há autorização DNIT.
    public double calcularPbtcEfetivo() {
        return dnit ? veiculo.getPbtc() * FATOR_DNIT : veiculo.getPbtc();
    }

    // Capacidade máxima em litros, respeitando a expansão do produto.
    public double calcularCapacidadeMaximaLitros() {
        return produto.calcularVolumeMaximo(capacidadeTanqueLitros);
    }

    // Limite 1: PBTC efetivo - tara.
    public double calcularLimitePeso() {
        return calcularPbtcEfetivo() - tara;
    }

    // Limite 2: volume máximo convertido em kg pela densidade.
    public double calcularLimiteVolume() {
        return calcularCapacidadeMaximaLitros() * produto.getDensidade();
    }

    // Limite 3: pedido com tolerância (ou teto regulatório sem pedido).
    // A tolerância é guardada "pura" (10 = 10%) e só aqui vira /100.
    public double calcularLimitePedido() {
        if (pedidoKg > 0 && toleranciaPedidoPercentual > 0) {
            return pedidoKg * (1 + toleranciaPedidoPercentual / 100.0);
        }
        if (pedidoKg > 0) {
            return pedidoKg;
        }
        return TETO_SEM_PEDIDO_KG;
    }

    // --------------------------------------------------------
    // RESULTADOS
    // --------------------------------------------------------
    public double calcularPesoLiquido() {
        return Math.min(calcularLimitePeso(),
                Math.min(calcularLimiteVolume(), calcularLimitePedido()));
    }

    public double calcularPesoTotal() {
        return tara + calcularPesoLiquido();
    }

    public double calcularLitros() {
        return calcularPesoLiquido() / produto.getDensidade();
    }

    public double calcularPercentualOcupacao() {
        return calcularLitros() / capacidadeTanqueLitros * 100.0;
    }

    // Diz qual limite "mandou" (útil para explicar o resultado).
    public String fatorLimitante() {
        double liquido = calcularPesoLiquido();
        if (Math.abs(liquido - calcularLimitePeso()) < EPS) {
            return "PESO (PBTC)";
        }
        if (Math.abs(liquido - calcularLimiteVolume()) < EPS) {
            return "VOLUME (tanque)";
        }
        return pedidoKg > 0 ? "PEDIDO (cliente)" : "TETO REGULATÓRIO";
    }

    // --------------------------------------------------------
    // VALIDAÇÃO COMPLETA (as 5 regras do LoadX)
    // --------------------------------------------------------
    public List<ResultadoValidacao> validar() {
        List<ResultadoValidacao> regras = new ArrayList<>();
        double litros = calcularLitros();
        double perc = calcularPercentualOcupacao();
        double minPerc = quebraOndas ? 80 : 90;

        // R1: PBTC
        if (calcularPesoTotal() > calcularPbtcEfetivo() + EPS) {
            regras.add(new ResultadoValidacao(
                    "Peso total excede o PBTC" + (dnit ? " + DNIT." : "."), false));
        } else {
            regras.add(new ResultadoValidacao("Peso total dentro do limite PBTC.", true));
        }

        // R2: ocupação volumétrica (mínimo 90%, ou 80% com quebra-ondas;
        //     ocupação até 20% é permitida como faixa baixa)
        if (perc > 100 + EPS) {
            regras.add(new ResultadoValidacao(
                    "Volume excede 100% — risco de transbordamento.", false));
        } else if (perc > 20 && perc < minPerc) {
            regras.add(new ResultadoValidacao("Ocupação " + dec1(perc)
                    + "% abaixo do mínimo (" + (int) minPerc + "%).", false));
        } else if (perc <= 20) {
            regras.add(new ResultadoValidacao("Ocupação " + dec1(perc)
                    + "% — faixa baixa permitida (até 20%).", true));
        } else {
            regras.add(new ResultadoValidacao("Ocupação " + dec1(perc)
                    + "% — dentro da faixa.", true));
        }

        // R3: pedido (facultativo)
        if (pedidoKg > 0) {
            double minPed = pedidoKg * (1 - toleranciaPedidoPercentual / 100.0);
            double maxPed = pedidoKg * (1 + toleranciaPedidoPercentual / 100.0);
            double liquido = calcularPesoLiquido();
            if (liquido < minPed - EPS) {
                regras.add(new ResultadoValidacao(
                        "Carga abaixo do pedido (mín. " + kg(minPed) + " kg).", false));
            } else if (liquido > maxPed + EPS) {
                regras.add(new ResultadoValidacao(
                        "Carga acima do pedido (máx. " + kg(maxPed) + " kg).", false));
            } else {
                regras.add(new ResultadoValidacao("Carga dentro da tolerância do pedido.", true));
            }
        } else {
            regras.add(new ResultadoValidacao(
                    "Pedido não informado — sem restrição de quantidade.", true));
        }

        // R4: mínimo de litros (facultativo)
        if (minimoLitros > 0) {
            if (litros < minimoLitros - EPS) {
                regras.add(new ResultadoValidacao(
                        "Volume abaixo do mínimo (" + kg(minimoLitros) + " L).", false));
            } else {
                regras.add(new ResultadoValidacao("Volume mínimo atingido.", true));
            }
        } else {
            regras.add(new ResultadoValidacao("Mínimo por carreta não informado.", true));
        }

        // R5: expansão do produto (só restringe se o produto reserva espaço)
        boolean produtoRestringe = calcularCapacidadeMaximaLitros() < capacidadeTanqueLitros - EPS;
        if (produtoRestringe) {
            if (litros > calcularCapacidadeMaximaLitros() + EPS) {
                regras.add(new ResultadoValidacao(
                        "Volume excede o limite de expansão do produto.", false));
            } else {
                regras.add(new ResultadoValidacao(
                        "Volume dentro do limite de expansão do produto.", true));
            }
        } else {
            regras.add(new ResultadoValidacao("Produto sem restrição de expansão.", true));
        }

        return regras;
    }

    private static String dec1(double v) { return String.format(BR, "%.1f", v); }

    private static String kg(double v) { return String.format(BR, "%,.0f", v); }

    // --------------------------------------------------------
    // GETTERS (somente leitura: o objeto é imutável)
    // --------------------------------------------------------
    public Veiculo getVeiculo() { return veiculo; }
    public Produto getProduto() { return produto; }
    public double getTara() { return tara; }
    public double getCapacidadeTanqueLitros() { return capacidadeTanqueLitros; }
    public boolean isDnit() { return dnit; }
    public boolean isQuebraOndas() { return quebraOndas; }
}
