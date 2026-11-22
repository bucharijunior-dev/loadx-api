package br.fatec.loadx.loadxapi.web;

import br.fatec.loadx.loadxapi.dominio.Veiculo;
import br.fatec.loadx.loadxapi.servico.VeiculoServico;
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
// VeiculoController.java  (NOVO)
// ------------------------------------------------------------
// O CONTROLLER recebe as requisições do navegador, chama o serviço
// e escolhe qual PÁGINA (template Thymeleaf) mostrar. É o equivalente
// aos eventos dos botões de um UserForm (cmd_..._Click).
//
// ANOTAÇÕES (o "como"):
//   @Controller              -> o Spring o trata como controlador web.
//   @RequestMapping("/veiculos") -> prefixo de endereço de todos os métodos.
//   @GetMapping / @PostMapping   -> qual endereço e qual método HTTP
//                                   (GET = abrir uma página; POST = enviar dados).
//   @PathVariable            -> pega o {id} do endereço.
//   @ModelAttribute("form")  -> preenche um VeiculoForm com os campos enviados.
//
// O QUE O MÉTODO RETORNA:
//   "veiculos/lista"           -> nome do template (templates/veiculos/lista.html)
//   "redirect:/veiculos"       -> manda o navegador abrir outro endereço
//
// O "Model" leva dados do controller para a página; o
// "RedirectAttributes" leva uma mensagem que sobrevive ao redirect.
// ============================================================
@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoServico servico;

    public VeiculoController(VeiculoServico servico) {
        this.servico = servico;
    }

    // GET /veiculos -> lista
    @GetMapping
    public String listar(Model model) {
        List<VeiculoForm> linhas = new ArrayList<>();
        for (Veiculo v : servico.listar()) {
            linhas.add(VeiculoForm.deDominio(v));
        }
        model.addAttribute("veiculos", linhas);
        return "veiculos/lista";
    }

    // GET /veiculos/novo -> formulário vazio
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new VeiculoForm());
        return "veiculos/form";
    }

    // GET /veiculos/5/editar -> formulário preenchido
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("form", VeiculoForm.deDominio(servico.buscar(id)));
            return "veiculos/form";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/veiculos";
        }
    }

    // POST /veiculos -> grava (novo ou edição)
    // Se algo for inválido, volta ao formulário mostrando o erro e
    // mantendo o que o usuário digitou.
    @PostMapping
    public String salvar(@ModelAttribute("form") VeiculoForm form,
                         Model model, RedirectAttributes redirect) {
        try {
            servico.salvar(form.paraDominio());
            redirect.addFlashAttribute("mensagem", "Veículo salvo com sucesso.");
            return "redirect:/veiculos";
        } catch (IllegalArgumentException | DataAccessException e) {
            model.addAttribute("erro", e.getMessage());
            return "veiculos/form";
        }
    }

    // POST /veiculos/5/excluir -> exclui
    // Se o veículo estiver em uso por uma programação, o banco recusa
    // (chave estrangeira) e mostramos uma mensagem.
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            servico.excluir(id);
            redirect.addFlashAttribute("mensagem", "Veículo excluído.");
        } catch (DataAccessException e) {
            redirect.addFlashAttribute("erro",
                    "Não foi possível excluir: o veículo está em uso por alguma programação.");
        }
        return "redirect:/veiculos";
    }
}
