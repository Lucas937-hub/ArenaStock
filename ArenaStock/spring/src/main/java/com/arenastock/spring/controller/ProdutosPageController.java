package com.arenastock.spring.controller;

// Marca como um controller de pagina
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
// Le os campos de um formulario e monta um objeto automaticamente
import org.springframework.web.bind.annotation.ModelAttribute;
// Le um pedaco da propria URL (ex: /produtos/editar/7 -> id=7)
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.arenastock.spring.model.Produto;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.service.CategoriaService;
import com.arenastock.spring.service.ProdutoService;

// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;

@Controller
public class ProdutosPageController {

    // Dependencias dos services, injetadas pelo Spring
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    // Construtor: recebe os dois services prontos
    public ProdutosPageController(ProdutoService produtoService,
                                  CategoriaService categoriaService){

        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }

    // GET /produtos -> exibe a tela com a lista de produtos e o modal de cadastro/edicao
    @GetMapping("/produtos")
    public String produtos(HttpServletRequest request, Model model){

        // Pega a sessao SE existir
        HttpSession session = request.getSession(false);

        // Sem sessao -> barra o acesso e manda pro login
        if(session == null){
            return "redirect:/login";
        }

        // Pega o usuario guardado na sessao
        Usuario usuario =
                (Usuario) session.getAttribute("usuarioLogado");

        // Sem usuario logado -> barra o acesso e manda pro login
        if(usuario == null){
            return "redirect:/login";
        }

        // Envia o usuario logado pro HTML (topo da tela)
        model.addAttribute("usuarioLogado", usuario);

        // Envia a lista de TODOS os produtos do sistema pro HTML montar a tabela
        model.addAttribute("produtos",
                produtoService.listarTodos());

        // Envia a lista de categorias pro HTML montar o <select> do modal
        model.addAttribute("categorias",
                categoriaService.listarTodas());

        // Envia um Produto vazio, usado pelo Thymeleaf como "molde" do formulario do modal
        model.addAttribute("produto",
                new Produto());

        // Nome do template que sera renderizado (produtos.html)
        return "produtos";
    }

    // POST /produtos -> recebe o formulario do modal "Novo Produto" e cadastra
    @PostMapping("/produtos")
public String cadastrarProduto(@ModelAttribute Produto produto,
                               HttpServletRequest request){

    // Pega a sessao SE existir
    HttpSession session = request.getSession(false);

    // Sem sessao -> barra o acesso e manda pro login
    if(session == null){
        return "redirect:/login";
    }

    // Pega o usuario guardado na sessao
    Usuario usuario =
            (Usuario) session.getAttribute("usuarioLogado");

    // Sem usuario logado -> barra o acesso (requisito: so cadastra produto logado)
    if(usuario == null){
        return "redirect:/login";
    }

    // Chama o service passando o produto, o id da categoria escolhida e o usuario logado
    produtoService.cadastrar(
            produto,
            produto.getCategoria().getId(),
            usuario);

    // Volta pra tela de produtos, ja com o novo produto na lista
    return "redirect:/produtos";
}

// POST /produtos/editar/{id} -> recebe o formulario do modal "Editar Produto" e atualiza
@PostMapping("/produtos/editar/{id}")
public String editarProduto(@PathVariable Long id,
                            @ModelAttribute Produto produto,
                            HttpServletRequest request){

    // Pega a sessao SE existir
    HttpSession session = request.getSession(false);

    // Sem sessao -> barra o acesso e manda pro login
    if(session == null){
        return "redirect:/login";
    }

    // Pega o usuario guardado na sessao
    Usuario usuario =
            (Usuario) session.getAttribute("usuarioLogado");

    // Sem usuario logado -> barra o acesso
    if(usuario == null){
        return "redirect:/login";
    }

    // Chama o service passando o id do produto e os dados novos
    produtoService.atualizar(id, produto);

    // Volta pra tela de produtos, ja com os dados atualizados
    return "redirect:/produtos";
}


}

