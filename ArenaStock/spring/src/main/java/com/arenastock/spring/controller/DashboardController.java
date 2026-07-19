package com.arenastock.spring.controller;

import com.arenastock.spring.model.Movimentacao;
import com.arenastock.spring.model.Produto;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.repository.CategoriaRepository;
import com.arenastock.spring.repository.MovimentacaoRepository;
import com.arenastock.spring.repository.ProdutoRepository;

// Marca como um controller de pagina
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.arenastock.spring.model.Usuario;
import java.util.List;

import org.springframework.ui.Model;
// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;


@Controller
public class DashboardController {
// Dependencias dos repositorios, injetadas pelo Spring
private final ProdutoRepository produtoRepository;
private final CategoriaRepository categoriaRepository;
private final MovimentacaoRepository movimentacaoRepository;

// Construtor: recebe os tres repositorios prontos
public DashboardController(
        ProdutoRepository produtoRepository,
        CategoriaRepository categoriaRepository,
        MovimentacaoRepository movimentacaoRepository) {

    this.produtoRepository = produtoRepository;
    this.categoriaRepository = categoriaRepository;
    this.movimentacaoRepository = movimentacaoRepository;
}

// GET /dashboard -> tela inicial depois do login, com os numeros gerais do sistema
@GetMapping("/dashboard")
public String dashboard(HttpServletRequest request, Model model) {

    // Pega a sessao SE existir
    HttpSession session = request.getSession(false);

    // Sem sessao ou sem usuario logado -> barra o acesso e manda pro login
    if (session == null || session.getAttribute("usuarioLogado") == null) {
        return "redirect:/login";
    }

// Conta quantos produtos existem no total
long totalProdutos = produtoRepository.count();
// Conta quantas categorias existem no total
long totalCategorias = categoriaRepository.count();

// Pega todas as movimentacoes e conta quantas sao do tipo ENTRADA
long totalEntradas = movimentacaoRepository.findAll()
        .stream()
        .filter(m -> m.getTipo().name().equals("ENTRADA"))
        .count();

// Pega todas as movimentacoes e conta quantas sao do tipo SAIDA
long totalSaidas = movimentacaoRepository.findAll()
        .stream()
        .filter(m -> m.getTipo().name().equals("SAIDA"))
        .count();

// Envia os numeros pro HTML mostrar nos cards
model.addAttribute("totalProdutos", totalProdutos);
model.addAttribute("totalCategorias", totalCategorias);
model.addAttribute("totalEntradas", totalEntradas);
model.addAttribute("totalSaidas", totalSaidas);

// Envia o usuario logado pro HTML (usado no topo da tela)
model.addAttribute(
        "usuarioLogado",
        session.getAttribute("usuarioLogado")
);

// Busca produtos com estoque baixo (quantidade <= 5), do menor pro maior
List<Produto> produtosBaixos =
        produtoRepository.findByQuantidadeLessThanEqualOrderByQuantidadeAsc(5);

// Envia essa lista pro HTML (card de "estoque baixo")
model.addAttribute("produtosBaixos", produtosBaixos);

// Busca as 5 movimentacoes mais recentes do sistema
List<Movimentacao> ultimasMovimentacoes =
        movimentacaoRepository.findTop5ByOrderByDataHoraDesc();

// Envia essa lista pro HTML (card de "ultimas movimentacoes")
model.addAttribute("ultimasMovimentacoes", ultimasMovimentacoes);

// Pega o usuario logado (variavel nao usada depois, so guardada aqui)
Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");


    // Envia o usuario logado de novo pro model (duplicado, mas inofensivo)
    model.addAttribute("usuarioLogado", session.getAttribute("usuarioLogado"));

    // Nome do template que sera renderizado (dashboard.html)
    return "dashboard";
}
}
