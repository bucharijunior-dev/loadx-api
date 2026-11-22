package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// Veiculo.java  (VERSÃO 3 — agora dentro do projeto Spring)
// ------------------------------------------------------------
// MUDANÇAS em relação à versão 2:
//   1) Primeira linha "package ...": agora a classe pertence ao
//      pacote "dominio" do projeto Spring. Pacote é como uma pasta
//      lógica que organiza as classes.
//   2) Novo campo "id": é o número que identifica o veículo no
//      banco de dados (chave primária). Fica vazio (null) até o
//      veículo ser gravado pela primeira vez.
//
// PILARES DA POO que aparecem neste arquivo:
//   - ABSTRAÇÃO ........ "abstract class" + método abstrato
//   - ENCAPSULAMENTO ... campos private + getters/setters COM validação
//   - HERANÇA .......... esta classe é a "mãe" (as filhas usam "extends")
//   - POLIMORFISMO ..... o método abstrato é implementado de
//                        formas diferentes em cada filha
//
// Todos os dados usados no projeto são FICTÍCIOS.
// ============================================================
public abstract class Veiculo {

    // --------------------------------------------------------
    // CAMPOS (ENCAPSULAMENTO)
    // --------------------------------------------------------
    private Long id;              // identificador no banco (null = ainda não gravado)
    private String descricao;
    private double pbtc;          // Peso Bruto Total Combinado, em kg
    private int numeroEixos;
    private double tara;          // Peso do veículo vazio, em kg

    // --------------------------------------------------------
    // CONSTRUTOR
    // Usa os SETTERS (e não atribui direto aos campos): assim a
    // validação vale também na criação do objeto.
    // O id não entra aqui: ele só passa a existir quando o banco
    // gera o número, na hora de gravar.
    // --------------------------------------------------------
    public Veiculo(String descricao, double pbtc, int numeroEixos, double tara) {
        setDescricao(descricao);
        setPbtc(pbtc);
        setNumeroEixos(numeroEixos);
        setTara(tara);
    }

    // --------------------------------------------------------
    // MÉTODO ABSTRATO (ABSTRAÇÃO + base do POLIMORFISMO)
    // Declara O QUÊ o veículo sabe fazer, sem dizer COMO.
    // Cada classe filha é OBRIGADA a implementá-lo.
    // --------------------------------------------------------
    public abstract double calcularCapacidadeLiquida();

    // --------------------------------------------------------
    // MÉTODO CONCRETO (comum a todos os veículos)
    // Chama o método abstrato: o Java descobre, em tempo de
    // execução, QUAL versão executar (isso é polimorfismo).
    // --------------------------------------------------------
    public boolean cargaCabe(double pesoCarga) {
        return pesoCarga <= calcularCapacidadeLiquida();
    }

    // --------------------------------------------------------
    // GETTERS E SETTERS
    // O id não tem validação: quem o define é o mapeador, a
    // partir do número gerado pelo banco.
    // --------------------------------------------------------
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição não pode ser vazia.");
        }
        this.descricao = descricao.trim();
    }

    public double getPbtc() { return pbtc; }

    public void setPbtc(double pbtc) {
        if (pbtc <= 0) {
            throw new IllegalArgumentException("O PBTC deve ser maior que zero.");
        }
        this.pbtc = pbtc;
    }

    public int getNumeroEixos() { return numeroEixos; }

    public void setNumeroEixos(int numeroEixos) {
        if (numeroEixos < 2) {
            throw new IllegalArgumentException("O veículo deve ter ao menos 2 eixos.");
        }
        this.numeroEixos = numeroEixos;
    }

    public double getTara() { return tara; }

    public void setTara(double tara) {
        if (tara < 0) {
            throw new IllegalArgumentException("A tara não pode ser negativa.");
        }
        this.tara = tara;
    }

    @Override
    public String toString() {
        return descricao + " | PBTC: " + pbtc + " kg | Eixos: " + numeroEixos
                + " | Tara: " + tara + " kg";
    }
}