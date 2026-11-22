package br.fatec.loadx.loadxapi.web;

import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.dominio.Programacao;
import br.fatec.loadx.loadxapi.dominio.Veiculo;
import br.fatec.loadx.loadxapi.servico.ProdutoServico;
import br.fatec.loadx.loadxapi.servico.ProgramacaoServico;
import br.fatec.loadx.loadxapi.servico.VeiculoServico;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ============================================================
// ProgramacaoController.java  (NOVO)
// ------------------------------------------------------------
// Telas da PROGRAMAÇÃO de carregamentos (a regra de negócio central):
//   GET  /programacoes                  -> lista (com litros máximos calculados)
//   GET  /programacoes/nova             -> formulário
//   POST /programacoes                  -> cria (nasce AGENDADA)
//   POST /programacoes/{id}/iniciar     -> AGENDADA -> EM_CARREGAMENTO
//   POST /programacoes/{id}/concluir    -> EM_CARREGAMENTO -> CONCLUIDA
//   POST /programacoes/{id}/cancelar    -> AGENDADA -> CANCELADA
//   POST /programacoes/{id}/excluir     -> exclui
//   GET/POST /programacoes/{id}/conferir-> confere o peso da balança
//
// As REGRAS (quem pode ir para qual status, quanto carregar) ficam no
// DOMÍNIO. Este controller só chama o serviço e mostra o resultado:
// se o domínio recusar (IllegalStateException), a mensagem vai à tela.
// ============================================================
@Controller
@RequestMapping("/programacoes")
public class ProgramacaoController {

    private final ProgramacaoServico servico;
    private final VeiculoServico veiculoServico;
    private final ProdutoServico produtoServico;

    public ProgramacaoController(ProgramacaoServico servico,
                                 VeiculoServico veiculoServico,
                                 ProdutoServico produtoServico) {
        this.servico = servico;
        this.veiculoServico = veiculoServico;
        this.produtoServico = produtoServico;
    }

    // ---------------- LISTA ----------------
    @GetMapping
    public String listar(Model model) {
        List<ProgramacaoLinha> linhas = new ArrayList<>();
        try {
            for (Programacao p : servico.listar()) {
                linhas.add(ProgramacaoLinha.de(p));
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
        }
        model.addAttribute("programacoes", linhas);
        return "programacoes/lista";
    }

    // ---------------- NOVA ----------------
    @GetMapping("/nova")
    public String nova(Model model) {
        ProgramacaoForm form = new ProgramacaoForm();
        // Sugere "agora" (sem segundos) no formato do campo datetime-local.
        form.setDataHoraPrevista(LocalDateTime.now().withSecond(0).withNano(0).toString());
        model.addAttribute("form", form);
        carregarOpcoes(model);
        return "programacoes/form";
    }

    @PostMapping
    public String criar(@ModelAttribute("form") ProgramacaoForm form,
                        Model model, RedirectAttributes redirect) {
        try {
            servico.criar(form.getOrdemTransporte(), form.exigirVeiculoId(), form.exigirProdutoId(),
                    form.exigirCapacidade(), form.converterDataHora());
            redirect.addFlashAttribute("mensagem", "Programação criada (AGENDADA).");
            return "redirect:/programacoes";
        } catch (DataIntegrityViolationException e) {
            // A coluna ordem_transporte é UNIQUE no banco.
            return voltarAoFormulario(model, "Já existe uma programação com essa ordem de transporte.");
        } catch (IllegalArgumentException | DataAccessException e) {
            return voltarAoFormulario(model, e.getMessage());
        }
    }

    // ---------------- MUDANÇAS DE STATUS E EXCLUSÃO ----------------
    @PostMapping("/{id}/iniciar")
    public String iniciar(@PathVariable Long id, RedirectAttributes redirect) {
        return executar(redirect, () -> servico.iniciar(id), "Carregamento iniciado.");
    }

    @PostMapping("/{id}/concluir")
    public String concluir(@PathVariable Long id, RedirectAttributes redirect) {
        return executar(redirect, () -> servico.concluir(id), "Programação concluída.");
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        return executar(redirect, () -> servico.cancelar(id), "Programação cancelada.");
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        return executar(redirect, () -> servico.excluir(id), "Programação excluída.");
    }

    // ---------------- CONFERÊNCIA DE PESO ----------------
    @GetMapping("/{id}/conferir")
    public String conferir(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("linha", ProgramacaoLinha.de(servico.buscar(id)));
            return "programacoes/conferir";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/programacoes";
        }
    }

    // Compara o peso medido na balança com o previsto, usando a
    // tolerância do produto (regra do domínio: pesoConfereComPrevisto).
    @PostMapping("/{id}/conferir")
    public String conferirPeso(@PathVariable Long id,
                               @RequestParam(required = false) Double pesoReal,
                               Model model, RedirectAttributes redirect) {
        try {
            Programacao p = servico.buscar(id);
            model.addAttribute("linha", ProgramacaoLinha.de(p));
            if (pesoReal == null || pesoReal <= 0) {
                model.addAttribute("erro", "Informe o peso real (maior que zero).");
            } else {
                model.addAttribute("pesoReal", pesoReal);
                model.addAttribute("confere", p.pesoConfereComPrevisto(pesoReal));
            }
            return "programacoes/conferir";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/programacoes";
        }
    }

    // ---------------- AUXILIARES ----------------
    // Executa uma ação e converte qualquer recusa em mensagem na tela.
    private String executar(RedirectAttributes redirect, Runnable acao, String sucesso) {
        try {
            acao.run();
            redirect.addFlashAttribute("mensagem", sucesso);
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (DataAccessException e) {
            redirect.addFlashAttribute("erro", "Não foi possível concluir a operação no banco de dados.");
        }
        return "redirect:/programacoes";
    }

    private String voltarAoFormulario(Model model, String erro) {
        model.addAttribute("erro", erro);
        carregarOpcoes(model);
        return "programacoes/form";
    }

    // Preenche as listas suspensas de veículos e produtos.
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
