package com.arenastock.spring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
// Envelope de resposta HTTP: status + corpo juntos
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
// Le um pedaco da propria URL
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// Le o JSON do corpo da requisicao
import org.springframework.web.bind.annotation.RequestBody;
// Prefixo de URL para todos os metodos
import org.springframework.web.bind.annotation.RequestMapping;
// Marca como API REST
import org.springframework.web.bind.annotation.RestController;

import com.arenastock.spring.model.Movimentacao;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.service.MovimentacaoService;

// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;
// Ativa as validacoes do model
import jakarta.validation.Valid;
// Toda rota aqui comeca com /api/movimentacao
@RestController
@RequestMapping("/api/movimentacao")
public class MovimentacaoController {

    // Dependencia do service, injetada pelo Spring
    private final MovimentacaoService movimentacaoService;

    // Construtor: recebe o service pronto
    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    // Metodo auxiliar: busca o usuario guardado na sessao, ou null se ninguem logado
    private Usuario obterUsuarioLogado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Usuario) session.getAttribute("usuarioLogado");
    }

    // POST /api/movimentacao -> registra entrada/saida (exige login)
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody Movimentacao movimentacao, HttpServletRequest request) {
        // Confere se tem alguem logado
        Usuario usuarioLogado = obterUsuarioLogado(request);
        if (usuarioLogado == null) {
            // Sem login -> devolve erro 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Voce precisa estar logado para registrar uma movimentacao."));
        }
        try {
            // Chama o service, que atualiza o estoque e salva a movimentacao
            Movimentacao salva = movimentacaoService.registrar(movimentacao, usuarioLogado);
            // Devolve 201 (Created) com a movimentacao salva
            return ResponseEntity.status(HttpStatus.CREATED).body(salva);
        } catch (RuntimeException e) {
            // Estoque insuficiente ou produto invalido -> devolve 400 com a mensagem
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // GET /api/movimentacao -> lista todas as movimentacoes
    @GetMapping
    public ResponseEntity<List<Movimentacao>> listar() {
        // Devolve 200 (OK) com a lista completa
        return ResponseEntity.ok(movimentacaoService.listarTodas());
    }

    // GET /api/movimentacao/produto/{produtoId} -> historico de um produto especifico
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<Movimentacao>> listarPorProduto(@PathVariable Long produtoId) {
        // Devolve 200 (OK) com o historico daquele produto
        return ResponseEntity.ok(movimentacaoService.listarPorProduto(produtoId));
    }

}
