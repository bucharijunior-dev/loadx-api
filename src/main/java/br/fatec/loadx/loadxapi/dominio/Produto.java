package br.fatec.loadx.loadxapi.dominio;

// ============================================================
// Produto.java  (VERSÃO 2 — agora dentro do projeto Spring)
// ------------------------------------------------------------
// MUDANÇAS em relação à versão anterior:
//   1) Linha "package ...dominio;": a classe passa a pertencer ao
//      pacote "dominio" do projeto Spring.
//   2) Novo campo "id": o número que identifica o produto no
//      banco (chave primária). Fica null até o produto ser
//      gravado pela primeira vez.
//
// Classe ABSTRATA que representa "um produto químico qualquer".
// Os tipos concretos (ProdutoComum e ProdutoComExpansao) herdam
// daqui e decidem COMO calcular o volume máximo de enchimento.
//
// PILARES DA POO neste arquivo:
//   - ABSTRAÇÃO ........ classe abstract + método abstrato
//   - ENCAPSULAMENTO ... campos private + setters que validam
//   - HERANÇA .......... filhas reaproveitam nome, densidade e tolerância
//   - POLIMORFISMO ..... calcularVolumeMaximo() muda conforme o tipo
//
// Todos os dados usados no projeto são FICTÍCIOS.
// ============================================================
public abstract class Produto {

    // --------------------------------------------------------
    // CAMPOS (ENCAPSULAMENTO)
    // --------------------------------------------------------
    private Long id;                        // identificador no banco (null = ainda não gravado)
    private String nome;
    private double densidade;               // em kg por litro (ex.: 0,80)
    private double toleranciaPercentual;    // 10 significa 10% (NÃO 0,10)

    // --------------------------------------------------------
    // CONSTRUTOR
    // Usa os setters para que a validação valha desde a criação.
    // --------------------------------------------------------
    public Produto(String nome, double densidade, double toleranciaPercentual) {
        setNome(nome);
        setDensidade(densidade);
        setToleranciaPercentual(toleranciaPercentual);
    }

    // --------------------------------------------------------
    // MÉTODO ABSTRATO (ABSTRAÇÃO + base do POLIMORFISMO)
    // Dada a capacidade do tanque do veículo, qual é o maior
    // volume que se pode carregar deste produto? Cada tipo de
    // produto responde de um jeito (ex.: produtos que dilatam
    // com a temperatura exigem reserva de espaço no tanque).
    // --------------------------------------------------------
    public abstract double calcularVolumeMaximo(double capacidadeTanqueLitros);

    // --------------------------------------------------------
    // MÉTODOS CONCRETOS (comuns a todos os produtos)
    // --------------------------------------------------------

    // Converte litros em quilos usando a densidade do produto.
    public double calcularPeso(double litros) {
        return litros * densidade;
    }

    // REGRA DA TOLERÂNCIA
    // O peso medido na balança é aceito se estiver dentro da faixa
    // "peso previsto ± tolerância%".
    //
    // Lição do projeto em VBA: o percentual é guardado como número
    // "puro" (10 para 10%) e a divisão por 100 acontece AQUI, na
    // hora de calcular. Assim o "%" nunca é dividido duas vezes.
    public boolean pesoDentroDaTolerancia(double pesoPrevisto, double pesoReal) {
        double margem = pesoPrevisto * (toleranciaPercentual / 100.0);
        double minimo = pesoPrevisto - margem;
        double maximo = pesoPrevisto + margem;
        return pesoReal >= minimo && pesoReal <= maximo;
    }

    // --------------------------------------------------------
    // GETTERS E SETTERS COM VALIDAÇÃO
    // O id não tem validação: quem o define é o mapeador, a
    // partir do número gerado pelo banco.
    // --------------------------------------------------------
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
        }
        this.nome = nome.trim().toUpperCase();   // padroniza em maiúsculas, como no LoadX
    }

    public double getDensidade() { return densidade; }

    public void setDensidade(double densidade) {
        if (densidade <= 0) {
            throw new IllegalArgumentException("A densidade deve ser maior que zero.");
        }
        this.densidade = densidade;
    }

    public double getToleranciaPercentual() { return toleranciaPercentual; }

    public void setToleranciaPercentual(double toleranciaPercentual) {
        if (toleranciaPercentual < 0 || toleranciaPercentual > 100) {
            throw new IllegalArgumentException("A tolerância deve estar entre 0 e 100 (%).");
        }
        this.toleranciaPercentual = toleranciaPercentual;
    }

    @Override
    public String toString() {
        return nome + " | Densidade: " + densidade + " kg/L | Tolerância: "
                + toleranciaPercentual + "%";
    }
}