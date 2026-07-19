package com.arenastock.spring.controller;

import org.springframework.http.HttpStatus;
// Envelope de resposta HTTP: permite controlar status + corpo juntos
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
// Le um pedaco da propria URL (ex: /api/produtos/7 -> id=7)
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
// Le um JSON enviado no corpo da requisicao (usado pelo fetch do JavaScript)
import org.springframework.web.bind.annotation.RequestBody;
// Le um parametro da URL (ex: ?categoriaId=3)
import org.springframework.web.bind.annotation.RequestParam;
// Marca a classe como API REST (devolve dados/JSON, nao paginas)
import org.springframework.web.bind.annotation.RestController;
// Define o prefixo de URL para todos os metodos desta classe
import org.springframework.web.bind.annotation.RequestMapping;
import com.arenastock.spring.model.Produto;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.service.ProdutoService;

// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;
// Ativa as validacoes do model (@NotBlank, @Positive etc.)
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

// Toda rota aqui comeca com /api/produtos
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    // Dependencia do service, injetada pelo Spring
    private final ProdutoService produtoService;

    // Construtor: recebe o service pronto
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // Metodo auxiliar: busca o usuario guardado na sessao.
    // Se nao existir (ninguem fez login nesta sessao), devolve null.
    private Usuario obterUsuarioLogado(HttpServletRequest request) {
        // Pega a sessao SE existir
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        // Devolve o usuario guardado na sessao
        return (Usuario) session.getAttribute("usuarioLogado");
    }

    // POST /api/produtos -> cadastra um produto, mas SO se houver um usuario
    // logado na sessao atual. Essa "if" e exatamente o requisito:
    // "um produto so pode ser cadastrado se o usuario estiver logado".
    @PostMapping
public ResponseEntity<?> cadastrar(

        // Le o JSON do corpo da requisicao e ja valida os campos do Produto
        @Valid @RequestBody Produto produto,

        // Le o id da categoria enviado como parametro na URL
        @RequestParam Long categoriaId,

        HttpServletRequest request) {
        // Confere se tem alguem logado na sessao
        Usuario usuarioLogado = obterUsuarioLogado(request);
        if (usuarioLogado == null) {
            // Sem login -> devolve erro 401 (nao autorizado)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Voce precisa estar logado para cadastrar um produto."));
        }

        // Chama o service pra cadastrar de fato
        Produto salvo =
        produtoService.cadastrar(
                produto,
                categoriaId,
                usuarioLogado);
        // Devolve 201 (Created) com o produto salvo no corpo da resposta
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // GET /api/produtos -> lista todos os produtos (exige login)
   @GetMapping
public ResponseEntity<?> listar(HttpServletRequest request){

    // Confere se tem alguem logado na sessao
    Usuario usuarioLogado = obterUsuarioLogado(request);

    if(usuarioLogado == null){
        // Sem login -> devolve erro 401
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro","Faça login."));
    }

    // Devolve 200 (OK) com a lista de todos os produtos
    return ResponseEntity.ok(produtoService.listarTodos());
}

    // GET /api/produtos/buscar?nome=xxx -> busca produtos pelo nome
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscar(@RequestParam String nome) {
        // Devolve 200 (OK) com a lista filtrada
        return ResponseEntity.ok(produtoService.buscarPorNome(nome));
    }

    // GET /api/produtos/{id} -> busca um produto especifico pelo id
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            // Devolve 200 (OK) com o produto encontrado
            return ResponseEntity.ok(produtoService.buscarPorId(id));
        } catch (RuntimeException e) {
            // Nao encontrou -> devolve 404 (Not Found) com a mensagem de erro
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    // PUT /api/produtos/{id} -> atualiza um produto (tambem exige login)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                        @Valid @RequestBody Produto dadosNovos,
                                        HttpServletRequest request) {
        // Sem login -> bloqueia com 401
        if (obterUsuarioLogado(request) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Voce precisa estar logado para atualizar um produto."));
        }
        try {
            // Devolve 200 (OK) com o produto ja atualizado
            return ResponseEntity.ok(produtoService.atualizar(id, dadosNovos));
        } catch (RuntimeException e) {
            // Produto nao encontrado -> devolve 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    // DELETE /api/produtos/{id} -> remove um produto (tambem exige login)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id, HttpServletRequest request) {
        // Sem login -> bloqueia com 401
        if (obterUsuarioLogado(request) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Voce precisa estar logado para remover um produto."));
        }
        try {
            // Remove o produto do banco
            produtoService.remover(id);
            // Devolve 204 (No Content) - deu certo, sem corpo de resposta
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Nao encontrou -> devolve 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }
}

