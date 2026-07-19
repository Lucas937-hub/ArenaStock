package com.arenastock.spring.controller;

import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.repository.MovimentacaoRepository;
import com.arenastock.spring.repository.ProdutoRepository;

// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;

// Marca como um controller de pagina
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Controller da PAGINA de movimentacoes (diferente do MovimentacaoController, que e a API REST)
@Controller
public class MovimentacoesController {

    // Dependencias dos repositorios, injetadas pelo Spring
    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    // Construtor: recebe os dois repositorios prontos
    public MovimentacoesController(MovimentacaoRepository movimentacaoRepository,
                                   ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    // GET /movimentacoes -> exibe a tela de movimentacoes
    @GetMapping("/movimentacoes")
    public String movimentacoes(HttpServletRequest request, Model model) {

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

        // Envia o usuario logado pro HTML (topo da tela)
        model.addAttribute("usuarioLogado", usuario);

        // Busca todas as movimentacoes, da mais recente pra mais antiga
        model.addAttribute("movimentacoes",
                movimentacaoRepository.findAllByOrderByDataHoraDesc());

        // Busca todos os produtos, pro HTML montar o <select> do modal
        model.addAttribute("produtos", produtoRepository.findAll());

        // Nome do template que sera renderizado (movimentacoes.html)
        return "movimentacoes";
    }
}

