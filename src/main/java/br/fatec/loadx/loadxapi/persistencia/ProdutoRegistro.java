package br.fatec.loadx.loadxapi.persistencia;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// ============================================================
// ProdutoRegistro.java  (NOVO)
// ------------------------------------------------------------
// ESPELHO DA TABELA "produto": cada campo é uma coluna.
// É o mesmo padrão do VeiculoRegistro:
//   - @Table("produto") liga a classe à tabela.
//   - @Id marca a chave primária (null = INSERT; com valor = UPDATE).
//   - camelCase vira snake_case: toleranciaPercentual ->
//     tolerancia_percentual.
//   - margemExpansaoPercentual é Double (maiúsculo) porque só o
//     produto "com expansão" a usa; no produto comum fica null.
//
// POR QUE NÃO GRAVAR Produto/ProdutoComum DIRETO: o Spring Data
// JDBC não mapeia hierarquias de classes, então usamos esta classe
// "achatada" e o ProdutoMapeador converte de/para o domínio.
// ============================================================
@Table("produto")
public class ProdutoRegistro {

    @Id
    private Long id;

    private String tipo;                         // "COMUM" ou "COM_EXPANSAO"
    private String nome;
    private double densidade;
    private double toleranciaPercentual;
    private Double margemExpansaoPercentual;     // só COM_EXPANSAO

    // Construtor vazio: o Spring o usa ao ler do banco.
    public ProdutoRegistro() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getDensidade() { return densidade; }
    public void setDensidade(double densidade) { this.densidade = densidade; }

    public double getToleranciaPercentual() { return toleranciaPercentual; }
    public void setToleranciaPercentual(double toleranciaPercentual) { this.toleranciaPercentual = toleranciaPercentual; }

    public Double getMargemExpansaoPercentual() { return margemExpansaoPercentual; }
    public void setMargemExpansaoPercentual(Double margemExpansaoPercentual) { this.margemExpansaoPercentual = margemExpansaoPercentual; }
}