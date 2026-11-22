package br.fatec.loadx.loadxapi.web;

import br.fatec.loadx.loadxapi.dominio.Produto;
import br.fatec.loadx.loadxapi.servico.ProdutoServico;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ============================================================
// ProdutoController.java  (NOVO)
// ------------------------------------------------------------
// Controller das telas de produto. Segue exatamente o mesmo desenho do
// VeiculoController (veja os comentários dele sobre as anotações):
//   GET  /produtos            -> lista
//   GET  /produtos/novo       -> formulário vazio
//   GET  /produtos/{id}/editar-> formulário preenchido
//   POST /produtos            -> grava (novo ou edição)
//   POST /produtos/{id}/excluir -> exclui
// ============================================================
@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoServico servico;

    public ProdutoController(ProdutoServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public String listar(Model model) {
        List<ProdutoForm> linhas = new ArrayList<>();
        for (Produto p : servico.listar()) {
            linhas.add(ProdutoForm.deDominio(p));
        }
        model.addAttribute("produtos", linhas);
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new ProdutoForm());
        return "produtos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("form", ProdutoForm.deDominio(servico.buscar(id)));
            return "produtos/form";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/produtos";
        }
    }

    @PostMapping
    public String salvar(@ModelAttribute("form") ProdutoForm form,
                         Model model, RedirectAttributes redirect) {
        try {
            servico.salvar(form.paraDominio());
            redirect.addFlashAttribute("mensagem", "Produto salvo com sucesso.");
            return "redirect:/produtos";
        } catch (IllegalArgumentException | DataAccessException e) {
            model.addAttribute("erro", e.getMessage());
            return "produtos/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            servico.excluir(id);
            redirect.addFlashAttribute("mensagem", "Produto excluído.");
        } catch (DataAccessException e) {
            redirect.addFlashAttribute("erro",
                    "Não foi possível excluir: o produto está em uso por alguma programação.");
        }
        return "redirect:/produtos";
    }
}
