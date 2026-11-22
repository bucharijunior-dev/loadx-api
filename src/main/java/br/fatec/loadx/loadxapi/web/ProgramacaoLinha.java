package br.fatec.loadx.loadxapi.web;

import br.fatec.loadx.loadxapi.dominio.Carregamento;
import br.fatec.loadx.loadxapi.dominio.Programacao;
import java.time.format.DateTimeFormatter;

// ============================================================
// ProgramacaoLinha.java  (NOVO)
// ------------------------------------------------------------
// Uma LINHA da tabela de programações, já pronta para exibir.
//
// POR QUE EXISTE: a tela precisa de dados que o domínio CALCULA
// (litros máximos, peso previsto, fator limitante). Em vez de pôr
// contas no HTML, o controller converte cada Programacao nesta
// classe simples e o template só mostra os valores.
// (Mesma ideia dos "Form" de veículo e produto: separar a tela do domínio.)
// ============================================================
public class ProgramacaoLinha {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long id;
    private String ordemTransporte;
    private String veiculo;
    private String produto;
    private String dataHora;
    private String status;            // nome do enum (AGENDADA...), usado nas regras de botões e na cor
    private String statusDescricao;   // texto legível ("Em carregamento")
    private double capacidadeTanqueLitros;
    private double litrosMaximos;
    private double pesoPrevisto;
    private double toleranciaPercentual;
    private String fatorLimitante;

    // DOMÍNIO -> LINHA. As contas vêm todas do domínio (Carregamento):
    // litros = menor entre limite por volume e limite por peso.
    public static ProgramacaoLinha de(Programacao p) {
        Carregamento c = p.montarCarregamento();
        ProgramacaoLinha l = new ProgramacaoLinha();
        l.id = p.getId();
        l.ordemTransporte = p.getOrdemTransporte();
        l.veiculo = p.getVeiculo().getDescricao();
        l.produto = p.getProduto().getNome();
        l.dataHora = p.getDataHoraPrevista().format(FORMATO);
        l.status = p.getStatus().name();
        l.statusDescricao = p.getStatus().getDescricao();
        l.capacidadeTanqueLitros = p.getCapacidadeTanqueLitros();
        l.litrosMaximos = c.calcularLitrosMaximos();
        l.pesoPrevisto = c.calcularPesoLiquido();
        l.toleranciaPercentual = p.getProduto().getToleranciaPercentual();
        l.fatorLimitante = c.fatorLimitante();
        return l;
    }

    public Long getId() { return id; }
    public String getOrdemTransporte() { return ordemTransporte; }
    public String getVeiculo() { return veiculo; }
    public String getProduto() { return produto; }
    public String getDataHora() { return dataHora; }
    public String getStatus() { return status; }
    public String getStatusDescricao() { return statusDescricao; }
    public double getCapacidadeTanqueLitros() { return capacidadeTanqueLitros; }
    public double getLitrosMaximos() { return litrosMaximos; }
    public double getPesoPrevisto() { return pesoPrevisto; }
    public double getToleranciaPercentual() { return toleranciaPercentual; }
    public String getFatorLimitante() { return fatorLimitante; }
}
