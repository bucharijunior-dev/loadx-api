package br.fatec.loadx.loadxapi.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// ============================================================
// HomeController.java  (NOVO)
// ------------------------------------------------------------
// Controller da página inicial. Ao abrir o endereço raiz ("/"), mostra
// o template templates/index.html.
// ============================================================
@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }
}
