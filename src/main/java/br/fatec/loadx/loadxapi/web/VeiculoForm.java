package br.fatec.loadx.loadxapi.web;

import br.fatec.loadx.loadxapi.dominio.Caminhao;
import br.fatec.loadx.loadxapi.dominio.Carreta;
import br.fatec.loadx.loadxapi.dominio.Veiculo;
import br.fatec.loadx.loadxapi.persistencia.VeiculoMapeador;

// ============================================================
// VeiculoForm.java  (NOVO)
// ------------------------------------------------------------
// Objeto que carrega os dados do FORMULÁRIO HTML de veículo.
//
// POR QUE EXISTE: o formulário é "plano" (todos os campos de uma vez,
// em texto), enquanto o domínio tem Caminhao e Carreta, cada um com
// seus campos e regras. O Thymeleaf liga cada campo da tela a uma
// propriedade desta classe (é o equivalente de ler as TextBox de um
// UserForm). Depois, paraDominio() converte para o objeto certo.
//
// Os campos numéricos usam as classes Double/Integer (e não double/int)
// porque um campo deixado em branco chega como null, e queremos
// detectar isso e avisar o usuário.
// ============================================================
public class VeiculoForm {

    private Long id;
    private String tipo;                  // "CAMINHAO" ou "CARRETA"
    private String descricao;
    private Double pbtc;
    private Integer numeroEixos;
    private Double tara;
    private Double capacidadeLitros;      // só CAMINHAO
    private Double taraSemirreboque;      // só CARRETA
    private Double capacidadeLiquida;     // SOMENTE EXIBIÇÃO na lista (calculada pelo domínio)

    // --------------------------------------------------------
    // FORMULÁRIO -> DOMÍNIO
    // Cria Caminhao ou Carreta conforme o tipo. Os setters do domínio
    // validam os valores (ex.: PBTC maior que zero) e lançam
    // IllegalArgumentException, que o controller mostra na tela.
    // --------------------------------------------------------
    public Veiculo paraDominio() {
        exigir(tipo, "Tipo");
        Veiculo veiculo;

        switch (tipo) {
            case VeiculoMapeador.TIPO_CAMINHAO -> veiculo = new Caminhao(
                    descricao, exigir(pbtc, "PBTC"), exigir(numeroEixos, "Número de eixos"),
                    exigir(tara, "Tara"), zeroSeVazio(capacidadeLitros));
            case VeiculoMapeador.TIPO_CARRETA -> veiculo = new Carreta(
                    descricao, exigir(pbtc, "PBTC"), exigir(numeroEixos, "Número de eixos"),
                    exigir(tara, "Tara do cavalo"), exigir(taraSemirreboque, "Tara do semirreboque"));
            default -> throw new IllegalArgumentException("Tipo de veículo inválido.");
        }

        veiculo.setId(id);
        return veiculo;
    }

    // --------------------------------------------------------
    // DOMÍNIO -> FORMULÁRIO (para editar e para listar)
    // --------------------------------------------------------
    public static VeiculoForm deDominio(Veiculo v) {
        VeiculoForm f = new VeiculoForm();
        f.id = v.getId();
        f.descricao = v.getDescricao();
        f.pbtc = v.getPbtc();
        f.numeroEixos = v.getNumeroEixos();
        f.tara = v.getTara();
        f.capacidadeLiquida = v.calcularCapacidadeLiquida();   // polimorfismo em ação

        if (v instanceof Caminhao c) {
            f.tipo = VeiculoMapeador.TIPO_CAMINHAO;
            f.capacidadeLitros = c.getCapacidadeLitros();
        } else if (v instanceof Carreta c) {
            f.tipo = VeiculoMapeador.TIPO_CARRETA;
            f.taraSemirreboque = c.getTaraSemirreboque();
        }
        return f;
    }

    // Campo opcional: em branco vale zero (ex.: capacidade do caminhão,
    // que é informada em cada programação ou cálculo).
    private static double zeroSeVazio(Double valor) {
        return valor == null ? 0.0 : valor;
    }

    // Se o valor veio vazio, recusa com uma mensagem clara.
    private static <T> T exigir(T valor, String campo) {
        if (valor == null || (valor instanceof String s && s.isBlank())) {
            throw new IllegalArgumentException("Preencha o campo: " + campo + ".");
        }
        return valor;
    }

    // Tara total do conjunto (para sugerir na tela de cálculo):
    // PBTC - capacidade líquida = tara do caminhão, ou cavalo + semirreboque.
    public Double getTaraTotal() {
        if (pbtc == null || capacidadeLiquida == null) {
            return null;
        }
        return Math.round((pbtc - capacidadeLiquida) * 100.0) / 100.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getPbtc() { return pbtc; }
    public void setPbtc(Double pbtc) { this.pbtc = pbtc; }

    public Integer getNumeroEixos() { return numeroEixos; }
    public void setNumeroEixos(Integer numeroEixos) { this.numeroEixos = numeroEixos; }

    public Double getTara() { return tara; }
    public void setTara(Double tara) { this.tara = tara; }

    public Double getCapacidadeLitros() { return capacidadeLitros; }
    public void setCapacidadeLitros(Double capacidadeLitros) { this.capacidadeLitros = capacidadeLitros; }

    public Double getTaraSemirreboque() { return taraSemirreboque; }
    public void setTaraSemirreboque(Double taraSemirreboque) { this.taraSemirreboque = taraSemirreboque; }

    public Double getCapacidadeLiquida() { return capacidadeLiquida; }
    public void setCapacidadeLiquida(Double capacidadeLiquida) { this.capacidadeLiquida = capacidadeLiquida; }
}
