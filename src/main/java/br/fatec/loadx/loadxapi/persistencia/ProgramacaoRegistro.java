package br.fatec.loadx.loadxapi.persistencia;

import br.fatec.loadx.loadxapi.dominio.StatusProgramacao;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// ============================================================
// ProgramacaoRegistro.java  (NOVO)
// ------------------------------------------------------------
// ESPELHO DA TABELA "programacao". Mesmo padrão dos outros registros,
// com três novidades:
//
//  1) REFERÊNCIAS POR ID: veiculoId e produtoId guardam apenas o
//     número do veículo e do produto (chaves estrangeiras). No
//     Spring Data JDBC, cada tabela principal é um "agregado"
//     independente, e a ligação entre agregados é feita por id,
//     não carregando o objeto inteiro. Quem monta o objeto completo
//     (Programacao com Veiculo e Produto) é o ProgramacaoServico.
//
//  2) ENUM: o campo status usa diretamente o enum StatusProgramacao.
//     O Spring grava o NOME do valor ("AGENDADA", "CONCLUIDA"...) na
//     coluna de texto, o que casa com o CHECK do schema.sql.
//
//  3) DATA E HORA: LocalDateTime corresponde à coluna TIMESTAMP.
//
// Lembrete dos nomes: camelCase vira snake_case
// (ordemTransporte -> ordem_transporte, veiculoId -> veiculo_id...).
// ============================================================
@Table("programacao")
public class ProgramacaoRegistro {

    @Id
    private Long id;

    private String ordemTransporte;
    private Long veiculoId;
    private Long produtoId;
    private double capacidadeTanqueLitros;
    private LocalDateTime dataHoraPrevista;
    private StatusProgramacao status;

    // Construtor vazio: o Spring o usa ao ler do banco.
    public ProgramacaoRegistro() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrdemTransporte() { return ordemTransporte; }
    public void setOrdemTransporte(String ordemTransporte) { this.ordemTransporte = ordemTransporte; }

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public double getCapacidadeTanqueLitros() { return capacidadeTanqueLitros; }
    public void setCapacidadeTanqueLitros(double capacidadeTanqueLitros) { this.capacidadeTanqueLitros = capacidadeTanqueLitros; }

    public LocalDateTime getDataHoraPrevista() { return dataHoraPrevista; }
    public void setDataHoraPrevista(LocalDateTime dataHoraPrevista) { this.dataHoraPrevista = dataHoraPrevista; }

    public StatusProgramacao getStatus() { return status; }
    public void setStatus(StatusProgramacao status) { this.status = status; }
}
