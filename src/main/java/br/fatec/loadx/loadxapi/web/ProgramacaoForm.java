package br.fatec.loadx.loadxapi.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

// ============================================================
// ProgramacaoForm.java  (NOVO)
// ------------------------------------------------------------
// Dados do FORMULÁRIO de nova programação. A tela envia só os IDs do
// veículo e do produto escolhidos nas listas suspensas; o serviço
// busca os objetos completos.
//
// A data/hora chega como TEXTO ("2026-11-23T08:30", formato do campo
// HTML datetime-local) e é convertida aqui, com mensagem amigável se
// vier vazia ou inválida.
// ============================================================
public class ProgramacaoForm {

    private String ordemTransporte;
    private Long veiculoId;
    private Long produtoId;
    private Double capacidadeTanqueLitros;
    private String dataHoraPrevista;

    public Long exigirVeiculoId() { return exigir(veiculoId, "Veículo"); }

    public Long exigirProdutoId() { return exigir(produtoId, "Produto"); }

    public double exigirCapacidade() { return exigir(capacidadeTanqueLitros, "Capacidade do tanque (L)"); }

    public LocalDateTime converterDataHora() {
        exigir(dataHoraPrevista, "Data/hora prevista");
        try {
            return LocalDateTime.parse(dataHoraPrevista);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data/hora prevista inválida.");
        }
    }

    private static <T> T exigir(T valor, String campo) {
        if (valor == null || (valor instanceof String s && s.isBlank())) {
            throw new IllegalArgumentException("Preencha o campo: " + campo + ".");
        }
        return valor;
    }

    public String getOrdemTransporte() { return ordemTransporte; }
    public void setOrdemTransporte(String v) { this.ordemTransporte = v; }

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long v) { this.veiculoId = v; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long v) { this.produtoId = v; }

    public Double getCapacidadeTanqueLitros() { return capacidadeTanqueLitros; }
    public void setCapacidadeTanqueLitros(Double v) { this.capacidadeTanqueLitros = v; }

    public String getDataHoraPrevista() { return dataHoraPrevista; }
    public void setDataHoraPrevista(String v) { this.dataHoraPrevista = v; }
}
