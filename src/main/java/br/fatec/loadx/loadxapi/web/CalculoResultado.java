package br.fatec.loadx.loadxapi.web;

import br.fatec.loadx.loadxapi.dominio.CalculoCarga;
import br.fatec.loadx.loadxapi.dominio.ResultadoValidacao;
import java.util.List;

// ============================================================
// CalculoResultado.java  (NOVO)
// ------------------------------------------------------------
// Resultado do cálculo já pronto para a tela (mesma ideia da
// ProgramacaoLinha): o controller converte o CalculoCarga do domínio
// nesta classe simples e o template só mostra os valores.
// ============================================================
public class CalculoResultado {

    private String veiculo;
    private String produto;
    private double tara;
    private double pbtcEfetivo;
    private double limitePeso;
    private double limiteVolume;
    private double limitePedido;
    private double pesoLiquido;
    private double pesoTotal;
    private double litros;
    private double ocupacao;
    private String fatorLimitante;
    private List<ResultadoValidacao> validacoes;
    private boolean aprovado;     // true se TODAS as regras passaram

    public static CalculoResultado de(CalculoCarga c) {
        CalculoResultado r = new CalculoResultado();
        r.veiculo = c.getVeiculo().getDescricao();
        r.produto = c.getProduto().getNome();
        r.tara = c.getTara();
        r.pbtcEfetivo = c.calcularPbtcEfetivo();
        r.limitePeso = c.calcularLimitePeso();
        r.limiteVolume = c.calcularLimiteVolume();
        r.limitePedido = c.calcularLimitePedido();
        r.pesoLiquido = c.calcularPesoLiquido();
        r.pesoTotal = c.calcularPesoTotal();
        r.litros = c.calcularLitros();
        r.ocupacao = c.calcularPercentualOcupacao();
        r.fatorLimitante = c.fatorLimitante();
        r.validacoes = c.validar();
        r.aprovado = r.validacoes.stream().allMatch(ResultadoValidacao::isAprovado);
        return r;
    }

    public String getVeiculo() { return veiculo; }
    public String getProduto() { return produto; }
    public double getTara() { return tara; }
    public double getPbtcEfetivo() { return pbtcEfetivo; }
    public double getLimitePeso() { return limitePeso; }
    public double getLimiteVolume() { return limiteVolume; }
    public double getLimitePedido() { return limitePedido; }
    public double getPesoLiquido() { return pesoLiquido; }
    public double getPesoTotal() { return pesoTotal; }
    public double getLitros() { return litros; }
    public double getOcupacao() { return ocupacao; }
    public String getFatorLimitante() { return fatorLimitante; }
    public List<ResultadoValidacao> getValidacoes() { return validacoes; }
    public boolean isAprovado() { return aprovado; }
}
