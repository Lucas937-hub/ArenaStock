package com.arenastock.spring.controller;

import org.springframework.http.HttpStatus;
// Envelope de resposta HTTP: status + corpo juntos
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
// Le um pedaco da propria URL
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// Le o JSON do corpo da requisicao
import org.springframework.web.bind.annotation.RequestBody;
// Marca como API REST
import org.springframework.web.bind.annotation.RestController;
// Prefixo de URL para todos os metodos
import org.springframework.web.bind.annotation.RequestMapping;

import com.arenastock.spring.model.Categoria;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.service.CategoriaService;

// Requisicao HTTP (usada pra acessar a sessao e o IP de origem)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;
// Ativa as validacoes do model
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

// Toda rota aqui comeca com /api/categorias
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    // Dependencia do service, injetada pelo Spring
    private final CategoriaService categoriaService;

    // Construtor: recebe o service pronto
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Metodo auxiliar: busca o usuario guardado na sessao, ou null se ninguem logado
    private Usuario obterUsuarioLogado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Usuario) session.getAttribute("usuarioLogado");
    }

    // POST /api/categorias -> cadastra uma nova categoria (exige login)
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody Categoria categoria, HttpServletRequest request) {
        // Confere se tem alguem logado na sessao. Antes esta checagem nao existia,
        // permitindo criar categorias sem identificar quem fez isso.
        Usuario usuarioLogado = obterUsuarioLogado(request);
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Voce precisa estar logado para cadastrar uma categoria."));
        }
        try {
            // Devolve 201 (Created) com a categoria salva (usuario e IP vao pro log de auditoria)
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(categoriaService.cadastrar(categoria, usuarioLogado, request.getRemoteAddr()));
        } catch (RuntimeException e) {
            // Nome duplicado -> devolve 400 (Bad Request) com a mensagem de erro
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/categorias -> lista todas as categorias
    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        // Devolve 200 (OK) com a lista completa
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    // DELETE /api/categorias/{id} -> remove uma categoria pelo id (exige login)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id, HttpServletRequest request) {
        // Confere se tem alguem logado na sessao. Antes esta checagem nao existia,
        // permitindo excluir categorias sem identificar quem fez isso.
        Usuario usuarioLogado = obterUsuarioLogado(request);
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Voce precisa estar logado para remover uma categoria."));
        }
        try {
            // Remove a categoria do banco (usuario e IP vao pro log de auditoria)
            categoriaService.remover(id, usuarioLogado, request.getRemoteAddr());
            // Devolve 204 (No Content) - deu certo, sem corpo de resposta
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Nao encontrou (ou tem produto vinculado) -> devolve 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }
}