package br.fatec.loadx.loadxapi.web;

import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.dominio.Veiculo;
import br.fatec.loadx.loadxapi.servico.CalculoServico;
import br.fatec.loadx.loadxapi.servico.ProdutoServico;
import br.fatec.loadx.loadxapi.servico.VeiculoServico;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// ============================================================
// CalculoController.java  (NOVO)
// ------------------------------------------------------------
// Tela de CÁLCULO (equivalente ao frm_CALCULO do VBA):
//   GET  /calculo -> formulário vazio
//   POST /calculo -> calcula e mostra o resultado NA MESMA PÁGINA,
//                    mantendo o que foi digitado.
// O cálculo em si fica no domínio (CalculoCarga); aqui só se recebem os
// dados, chama-se o serviço e escolhe-se a página.
// ============================================================
@Controller
@RequestMapping("/calculo")
public class CalculoController {

    private final CalculoServico servico;
    private final VeiculoServico veiculoServico;
    private final ProdutoServico produtoServico;

    public CalculoController(CalculoServico servico, VeiculoServico veiculoServico,
                             ProdutoServico produtoServico) {
        this.servico = servico;
        this.veiculoServico = veiculoServico;
        this.produtoServico = produtoServico;
    }

    @GetMapping
    public String abrir(Model model) {
        model.addAttribute("form", new CalculoForm());
        carregarOpcoes(model);
        return "calculo/calculo";
    }

    @PostMapping
    public String calcular(@ModelAttribute("form") CalculoForm form, Model model) {
        try {
            model.addAttribute("resultado", CalculoResultado.de(servico.calcular(
                    form.exigirVeiculoId(), form.exigirProdutoId(), form.exigirTara(),
                    form.exigirCapacidade(), form.pedidoOuZero(), form.toleranciaOuZero(),
                    form.minimoOuZero(), form.isDnit(), form.isQuebraOndas())));
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
        }
        carregarOpcoes(model);
        return "calculo/calculo";
    }

    // Preenche as listas suspensas.
    private void carregarOpcoes(Model model) {
        List<VeiculoForm> veiculos = new ArrayList<>();
        for (Veiculo v : veiculoServico.listar()) {
            veiculos.add(VeiculoForm.deDominio(v));
        }
        List<ProdutoForm> produtos = new ArrayList<>();
        for (Produto p : produtoServico.listar()) {
            produtos.add(ProdutoForm.deDominio(p));
        }
        model.addAttribute("veiculos", veiculos);
        model.addAttribute("produtos", produtos);
    }
}
