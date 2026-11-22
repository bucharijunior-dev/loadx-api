package br.fatec.loadx.loadxapi.dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ============================================================
// Programacao.java  (VERSÃO 2 — agora dentro do projeto Spring)
// ------------------------------------------------------------
// MUDANÇAS em relação à versão anterior:
//   1) Linha "package ...dominio;".
//   2) Novo campo "id" (chave primária no banco; null até gravar).
//   3) Novo método estático "restaurar(...)": recria uma programação
//      que JÁ EXISTE no banco, com o status que ela tinha.
//
// Representa o AGENDAMENTO de um carregamento: qual veículo vai
// carregar qual produto, quando, e em que situação está.
//
// CONCEITOS NESTE ARQUIVO:
//   - COMPOSIÇÃO ....... uma Programacao "tem um" Veiculo e "tem um" Produto
//   - ENUM ............. StatusProgramacao no lugar de texto livre
//   - REGRA DE NEGÓCIO . o status só avança em ordem válida
//   - ENCAPSULAMENTO ... o status NÃO tem setter público: só muda
//                        pelos métodos iniciar(), concluir() e cancelar()
// ============================================================
public class Programacao {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long id;                          // identificador no banco (null = ainda não gravada)
    private String ordemTransporte;           // identificador de negócio (como a OT do LoadX)
    private Veiculo veiculo;
    private Produto produto;
    private double capacidadeTanqueLitros;
    private LocalDateTime dataHoraPrevista;
    private StatusProgramacao status;

    // --------------------------------------------------------
    // CONSTRUTOR
    // Toda programação NOVA nasce como AGENDADA.
    // --------------------------------------------------------
    public Programacao(String ordemTransporte, Veiculo veiculo, Produto produto,
                       double capacidadeTanqueLitros, LocalDateTime dataHoraPrevista) {
        setOrdemTransporte(ordemTransporte);
        setVeiculo(veiculo);
        setProduto(produto);
        setCapacidadeTanqueLitros(capacidadeTanqueLitros);
        setDataHoraPrevista(dataHoraPrevista);
        this.status = StatusProgramacao.AGENDADA;
    }

    // --------------------------------------------------------
    // FÁBRICA PARA RESTAURAR DO BANCO
    // Problema: ao ler uma programação gravada (por exemplo, já
    // CONCLUIDA), não podemos usar o construtor, porque ele sempre
    // começa em AGENDADA; e não existe setStatus() de propósito.
    // Solução: este método estático, que fica DENTRO da classe e por
    // isso pode preencher o campo privado "status". Ele deve ser
    // usado só pelo mapeador, ao ler do banco; para o resto do
    // sistema, o status continua só mudando pelas regras abaixo.
    // --------------------------------------------------------
    public static Programacao restaurar(Long id, String ordemTransporte, Veiculo veiculo,
                                        Produto produto, double capacidadeTanqueLitros,
                                        LocalDateTime dataHoraPrevista, StatusProgramacao status) {
        Programacao p = new Programacao(ordemTransporte, veiculo, produto,
                capacidadeTanqueLitros, dataHoraPrevista);
        p.id = id;
        p.status = status;
        return p;
    }

    // --------------------------------------------------------
    // REGRAS DE TRANSIÇÃO DE STATUS (REGRA DE NEGÓCIO)
    //   AGENDADA -> EM_CARREGAMENTO -> CONCLUIDA
    //   AGENDADA -> CANCELADA          (só antes de iniciar)
    // Qualquer outra tentativa lança exceção.
    // --------------------------------------------------------
    public void iniciarCarregamento() {
        if (status != StatusProgramacao.AGENDADA) {
            throw new IllegalStateException(
                    "Só é possível iniciar uma programação AGENDADA (atual: "
                    + status.getDescricao() + ").");
        }
        status = StatusProgramacao.EM_CARREGAMENTO;
    }

    public void concluir() {
        if (status != StatusProgramacao.EM_CARREGAMENTO) {
            throw new IllegalStateException(
                    "Só é possível concluir uma programação EM CARREGAMENTO (atual: "
                    + status.getDescricao() + ").");
        }
        status = StatusProgramacao.CONCLUIDA;
    }

    public void cancelar() {
        if (status != StatusProgramacao.AGENDADA) {
            throw new IllegalStateException(
                    "Só é possível cancelar uma programação AGENDADA (atual: "
                    + status.getDescricao() + ").");
        }
        status = StatusProgramacao.CANCELADA;
    }

    // --------------------------------------------------------
    // CÁLCULOS (reaproveitam as classes já prontas)
    // --------------------------------------------------------
    public Carregamento montarCarregamento() {
        return new Carregamento(veiculo, produto, capacidadeTanqueLitros);
    }

    public double calcularPesoPrevisto() {
        return montarCarregamento().calcularPesoLiquido();
    }

    // Confere o peso medido na balança com o previsto, usando a
    // tolerância do produto.
    public boolean pesoConfereComPrevisto(double pesoReal) {
        return produto.pesoDentroDaTolerancia(calcularPesoPrevisto(), pesoReal);
    }

    // --------------------------------------------------------
    // GETTERS E SETTERS (com validação)
    // Repare: NÃO existe setStatus() — de propósito.
    // --------------------------------------------------------
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getOrdemTransporte() { return ordemTransporte; }

    public void setOrdemTransporte(String ordemTransporte) {
        if (ordemTransporte == null || ordemTransporte.trim().isEmpty()) {
            throw new IllegalArgumentException("A ordem de transporte não pode ser vazia.");
        }
        this.ordemTransporte = ordemTransporte.trim().toUpperCase();
    }

    public Veiculo getVeiculo() { return veiculo; }

    public void setVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new IllegalArgumentException("O veículo é obrigatório.");
        }
        this.veiculo = veiculo;
    }

    public Produto getProduto() { return produto; }

    public void setProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("O produto é obrigatório.");
        }
        this.produto = produto;
    }

    public double getCapacidadeTanqueLitros() { return capacidadeTanqueLitros; }

    public void setCapacidadeTanqueLitros(double capacidadeTanqueLitros) {
        if (capacidadeTanqueLitros <= 0) {
            throw new IllegalArgumentException("A capacidade do tanque deve ser maior que zero.");
        }
        this.capacidadeTanqueLitros = capacidadeTanqueLitros;
    }

    public LocalDateTime getDataHoraPrevista() { return dataHoraPrevista; }

    public void setDataHoraPrevista(LocalDateTime dataHoraPrevista) {
        if (dataHoraPrevista == null) {
            throw new IllegalArgumentException("A data/hora prevista é obrigatória.");
        }
        this.dataHoraPrevista = dataHoraPrevista;
    }

    public StatusProgramacao getStatus() { return status; }

    @Override
    public String toString() {
        return "OT " + ordemTransporte + " | " + veiculo.getDescricao()
                + " | " + produto.getNome()
                + " | " + dataHoraPrevista.format(FORMATO_DATA)
                + " | " + status.getDescricao();
    }
}
