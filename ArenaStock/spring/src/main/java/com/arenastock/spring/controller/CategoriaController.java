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
import com.arenastock.spring.service.CategoriaService;

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

    // POST /api/categorias -> cadastra uma nova categoria
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody Categoria categoria) {
        try {
            // Devolve 201 (Created) com a categoria salva
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.cadastrar(categoria));
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

    // DELETE /api/categorias/{id} -> remove uma categoria pelo id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        try {
            // Remove a categoria do banco
            categoriaService.remover(id);
            // Devolve 204 (No Content) - deu certo, sem corpo de resposta
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Nao encontrou (ou tem produto vinculado) -> devolve 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }
}
