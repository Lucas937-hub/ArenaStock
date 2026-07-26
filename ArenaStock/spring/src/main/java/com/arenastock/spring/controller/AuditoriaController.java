package com.arenastock.spring.controller;

import com.arenastock.spring.model.LogAuditoria;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.service.AuditoriaService;

import org.springframework.http.HttpStatus;
// Envelope de resposta HTTP: status + corpo juntos
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// Prefixo de URL para todos os metodos
import org.springframework.web.bind.annotation.RequestMapping;
// Marca como API REST
import org.springframework.web.bind.annotation.RestController;

// Requisicao HTTP (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

// Toda rota aqui comeca com /api/auditoria.
// Esta API existe pra alimentar a tela de Auditoria (via JS, se precisar) e
// pra permitir consultas pontuais, mas o acesso e SEMPRE restrito a usuarios
// com cargo ADMINISTRADOR - e essa e a regra de seguranca central deste recurso.
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    // Dependencia do service, injetada pelo Spring
    private final AuditoriaService auditoriaService;

    // Construtor: recebe o service pronto
    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    // Metodo auxiliar: busca o usuario guardado na sessao, ou null se ninguem logado
    private Usuario obterUsuarioLogado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Usuario) session.getAttribute("usuarioLogado");
    }

    // Metodo auxiliar: confere se o usuario logado e um administrador.
    // Centraliza a regra "auditoria e exclusiva de administrador" num unico lugar.
    private boolean naoEhAdministrador(Usuario usuario) {
        return usuario == null || !"ADMINISTRADOR".equalsIgnoreCase(usuario.getCargo());
    }

    // GET /api/auditoria -> lista todo o historico de auditoria (somente ADMINISTRADOR)
    @GetMapping
    public ResponseEntity<?> listar(HttpServletRequest request) {

        Usuario usuarioLogado = obterUsuarioLogado(request);

        // Sem login -> 401 (nao autenticado)
        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Você precisa estar logado."));
        }

        // Logado, mas nao e administrador -> 403 (autenticado, porem sem permissao)
        if (naoEhAdministrador(usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "Apenas administradores podem acessar o log de auditoria."));
        }

        List<LogAuditoria> logs = auditoriaService.listarTodos();
        return ResponseEntity.ok(logs);
    }

    // GET /api/auditoria/usuario/{usuarioId} -> historico de um usuario especifico (somente ADMINISTRADOR)
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId, HttpServletRequest request) {

        Usuario usuarioLogado = obterUsuarioLogado(request);

        if (usuarioLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Você precisa estar logado."));
        }

        if (naoEhAdministrador(usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "Apenas administradores podem acessar o log de auditoria."));
        }

        return ResponseEntity.ok(auditoriaService.listarPorUsuario(usuarioId));
    }
}

