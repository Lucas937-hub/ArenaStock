package com.arenastock.spring.controller;

import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.service.CategoriaService;

// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;

// Marca como um controller de pagina
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Controller antigo, de quando o cadastro de produto seria uma pagina separada
// (hoje o cadastro acontece via modal na tela de produtos)
@Controller
public class CadastroProdutoPageController {

    // Dependencia do service, injetada pelo Spring
    private final CategoriaService categoriaService;

    // Construtor: recebe o service pronto
    public CadastroProdutoPageController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // GET /produtos/novo -> rota antiga, mantida so pra nao dar erro se alguem acessar
    @GetMapping("/produtos/novo")
    public String abrirCadastro(Model model,
                                HttpServletRequest request) {

        // Pega a sessao SE existir
        HttpSession session = request.getSession(false);

        // Sem sessao -> manda pro login
        if(session == null){
            return "redirect:/login";
        }

        // Pega o usuario guardado na sessao
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        // Sem usuario logado -> manda pro login
        if(usuario == null){
            return "redirect:/login";
        }

        // Envia o usuario logado pro model (nao chega a ser usado, pois abaixo ja redireciona)
        model.addAttribute("usuarioLogado", usuario);

        // Envia a lista de categorias pro model (idem, nao chega a ser usado)
        model.addAttribute("categorias",
                categoriaService.listarTodas());

        // Como nao existe mais uma pagina separada de cadastro, redireciona pra tela de produtos
        return "redirect:/produtos";
    }

}
