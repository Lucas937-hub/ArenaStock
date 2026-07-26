package com.arenastock.spring.controller;

import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.service.AuditoriaService;

// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;

// Marca como um controller de pagina
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Controller da PAGINA de auditoria (diferente do AuditoriaController, que e a API REST).
// Exclusiva para usuarios com cargo ADMINISTRADOR - qualquer outro perfil e
// barrado e mandado de volta pro dashboard.
@Controller
public class AuditoriaPageController {

    // Dependencia do service, injetada pelo Spring
    private final AuditoriaService auditoriaService;

    // Construtor: recebe o service pronto
    public AuditoriaPageController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    // GET /auditoria -> exibe a tela com o historico completo de acoes do sistema
    @GetMapping("/auditoria")
    public String auditoria(HttpServletRequest request, Model model) {

        // Pega a sessao SE existir
        HttpSession session = request.getSession(false);

        // Sem sessao -> barra o acesso e manda pro login
        if (session == null) {
            return "redirect:/login";
        }

        // Pega o usuario guardado na sessao
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        // Sem usuario logado -> barra o acesso
        if (usuario == null) {
            return "redirect:/login";
        }

        // Logado, mas nao e administrador -> nao tem permissao pra ver esta tela,
        // entao volta pro dashboard em vez de mostrar o historico de todo mundo
        if (!"ADMINISTRADOR".equalsIgnoreCase(usuario.getCargo())) {
            return "redirect:/dashboard";
        }

        // Envia o usuario logado pro HTML (topo da tela)
        model.addAttribute("usuarioLogado", usuario);

        // Envia o historico completo de auditoria pro HTML montar a tabela
        model.addAttribute("logs", auditoriaService.listarTodos());

        // Nome do template que sera renderizado (auditoria.html)
        return "auditoria";
    }
}
