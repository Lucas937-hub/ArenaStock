package com.arenastock.spring.controller;

import com.arenastock.spring.model.Categoria;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.repository.ProdutoRepository;
import com.arenastock.spring.service.CategoriaService;

// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;

// Marca como um controller de pagina
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controller da PAGINA de categorias (diferente do CategoriaController, que e a API REST)
@Controller
public class CategoriasController {

    // Dependencias, injetadas pelo Spring
    private final CategoriaService categoriaService;
    private final ProdutoRepository produtoRepository;

    // Construtor: recebe o service e o repositorio prontos
    public CategoriasController(CategoriaService categoriaService,
                                ProdutoRepository produtoRepository) {
        this.categoriaService = categoriaService;
        this.produtoRepository = produtoRepository;
    }

    // GET /categorias -> exibe a tela de categorias
    @GetMapping("/categorias")
    public String categorias(HttpServletRequest request, Model model) {

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

        // Busca todas as categorias cadastradas
        List<Categoria> categorias = categoriaService.listarTodas();
        // Envia a lista pro HTML montar a tabela
        model.addAttribute("categorias", categorias);

        // Mapa que vai guardar: id da categoria -> quantidade de produtos daquela categoria
        Map<Long, Long> contagemProdutos = new HashMap<>();
        // Para cada categoria, conta quantos produtos usam ela e guarda no mapa
        for (Categoria categoria : categorias) {
            contagemProdutos.put(categoria.getId(),
                    produtoRepository.countByCategoriaId(categoria.getId()));
        }
        // Envia o mapa pro HTML mostrar a coluna "Produtos" da tabela
        model.addAttribute("contagemProdutos", contagemProdutos);

        // Nome do template que sera renderizado (categorias.html)
        return "categorias";
    }
}

