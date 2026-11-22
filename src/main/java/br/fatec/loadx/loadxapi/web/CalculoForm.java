package br.fatec.loadx.loadxapi.web;

// ============================================================
// CalculoForm.java  (NOVO)
// ------------------------------------------------------------
// Dados do formulário da tela de CÁLCULO (equivalente aos controles do
// frm_CALCULO: ComboBox de veículo/produto, TextBox de tara, pedido,
// tolerância, mínimo, e os interruptores DNIT e quebra-ondas).
// Campos opcionais em branco chegam como null e valem 0.
// ============================================================
public class CalculoForm {

    private Long veiculoId;
    private Long produtoId;
    private Double tara;
    private Double capacidadeTanqueLitros;
    private Double pedidoKg;
    private Double toleranciaPedidoPercentual;
    private Double minimoLitros;
    private boolean dnit;
    private boolean quebraOndas;

    public Long exigirVeiculoId() { return exigir(veiculoId, "Veículo"); }
    public Long exigirProdutoId() { return exigir(produtoId, "Produto"); }
    public double exigirTara() { return exigir(tara, "Tara (kg)"); }
    public double exigirCapacidade() { return exigir(capacidadeTanqueLitros, "Capacidade do tanque (L)"); }

    public double pedidoOuZero() { return zero(pedidoKg); }
    public double toleranciaOuZero() { return zero(toleranciaPedidoPercentual); }
    public double minimoOuZero() { return zero(minimoLitros); }

    private static double zero(Double v) { return v == null ? 0 : v; }

    private static <T> T exigir(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("Preencha o campo: " + campo + ".");
        }
        return valor;
    }

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long v) { this.veiculoId = v; }
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long v) { this.produtoId = v; }
    public Double getTara() { return tara; }
    public void setTara(Double v) { this.tara = v; }
    public Double getCapacidadeTanqueLitros() { return capacidadeTanqueLitros; }
    public void setCapacidadeTanqueLitros(Double v) { this.capacidadeTanqueLitros = v; }
    public Double getPedidoKg() { return pedidoKg; }
    public void setPedidoKg(Double v) { this.pedidoKg = v; }
    public Double getToleranciaPedidoPercentual() { return toleranciaPedidoPercentual; }
    public void setToleranciaPedidoPercentual(Double v) { this.toleranciaPedidoPercentual = v; }
    public Double getMinimoLitros() { return minimoLitros; }
    public void setMinimoLitros(Double v) { this.minimoLitros = v; }
    public boolean isDnit() { return dnit; }
    public void setDnit(boolean v) { this.dnit = v; }
    public boolean isQuebraOndas() { return quebraOndas; }
    public void setQuebraOndas(boolean v) { this.quebraOndas = v; }
}
