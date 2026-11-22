package br.fatec.loadx.loadxapi.persistencia;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// ============================================================
// VeiculoRegistro.java  (NOVO)
// ------------------------------------------------------------
// Esta classe é o ESPELHO DA TABELA "veiculo": cada campo
// corresponde a uma coluna, e cada objeto, a uma linha.
//
// POR QUE EXISTE (a pergunta do "porquê"):
//   O Spring Data JDBC não consegue gravar uma hierarquia de
//   classes (Veiculo -> Caminhao / Carreta). Ele precisa de uma
//   classe simples, "achatada", igual à tabela. Então o domínio
//   (com herança e regras) fica em "dominio", e esta classe só
//   carrega dados. O VeiculoMapeador converte de um para o outro.
//
// COMO FUNCIONA (a pergunta do "como"):
//   - @Table("veiculo") diz a qual tabela esta classe pertence.
//   - @Id marca a chave primária. Se for null, o Spring faz um
//     INSERT e preenche o id com o número gerado pelo banco; se
//     já tiver valor, faz um UPDATE.
//   - Os nomes em camelCase viram colunas em snake_case
//     automaticamente: numeroEixos -> numero_eixos.
//   - Campos específicos de um tipo (capacidadeLitros,
//     taraSemirreboque) usam Double (com letra maiúscula) porque
//     podem ser null: um caminhão não tem tara de semirreboque.
// ============================================================
@Table("veiculo")
public class VeiculoRegistro {

    @Id
    private Long id;

    private String tipo;                 // "CAMINHAO" ou "CARRETA"
    private String descricao;
    private double pbtc;
    private int numeroEixos;
    private double tara;
    private Double capacidadeLitros;     // só CAMINHAO
    private Double taraSemirreboque;     // só CARRETA

    // Construtor vazio: o Spring o usa para criar o objeto ao ler do banco.
    public VeiculoRegistro() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getPbtc() { return pbtc; }
    public void setPbtc(double pbtc) { this.pbtc = pbtc; }

    public int getNumeroEixos() { return numeroEixos; }
    public void setNumeroEixos(int numeroEixos) { this.numeroEixos = numeroEixos; }

    public double getTara() { return tara; }
    public void setTara(double tara) { this.tara = tara; }

    public Double getCapacidadeLitros() { return capacidadeLitros; }
    public void setCapacidadeLitros(Double capacidadeLitros) { this.capacidadeLitros = capacidadeLitros; }

    public Double getTaraSemirreboque() { return taraSemirreboque; }
    public void setTaraSemirreboque(Double taraSemirreboque) { this.taraSemirreboque = taraSemirreboque; }
}