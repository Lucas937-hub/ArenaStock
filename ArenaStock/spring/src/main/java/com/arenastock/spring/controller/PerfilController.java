package com.arenastock.spring.controller;

import com.arenastock.spring.model.Usuario;

// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;

// Marca como um controller de pagina
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerfilController {

    // GET /perfil -> exibe os dados do proprio usuario logado
    @GetMapping("/perfil")
    public String perfil(HttpServletRequest request, Model model) {

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

        // Envia o usuario logado pro HTML mostrar nome, login, e-mail e cargo
        model.addAttribute("usuarioLogado", usuario);

        // Nome do template que sera renderizado (perfil.html)
        return "perfil";
    }
}

