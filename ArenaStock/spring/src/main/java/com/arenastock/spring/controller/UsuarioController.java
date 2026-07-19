package com.arenastock.spring.controller;

// Marca como um controller de pagina (devolve nomes de template)
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// Le os campos de um formulario e monta um objeto automaticamente
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
// Le um parametro especifico da requisicao
import org.springframework.web.bind.annotation.RequestParam;

import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.service.UsuarioService;

// Representa a requisicao HTTP inteira (usada pra acessar a sessao)
import jakarta.servlet.http.HttpServletRequest;
// Representa a sessao do navegador (guarda quem esta logado)
import jakarta.servlet.http.HttpSession;
@Controller
public class UsuarioController {

    // Dependencia do service, injetada pelo Spring
    private final UsuarioService usuarioService;

    // Construtor: recebe o service pronto
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /cadastro-usuario -> recebe o formulario e cria um novo usuario
    @PostMapping("/cadastro-usuario")
public String cadastrar(@ModelAttribute Usuario usuario) {

    // Tenta cadastrar; se der erro (login/email duplicado), cai no catch
    try {
        usuarioService.cadastrar(usuario);
        // Deu certo: manda o navegador para a tela de login
        return "redirect:/login";
    } catch (RuntimeException e) {
        // Deu erro: volta pra tela de cadastro com "?erro" na URL
        return "redirect:/cadastro-usuario?erro";
    }
}

    // POST /login -> autentica o usuario e guarda ele na sessao
@PostMapping("/login")
public String login(@RequestParam String login,
                    @RequestParam String senha,
                    HttpServletRequest request) {

    // Tenta autenticar; se der erro (login/senha errados), cai no catch
    try {
        Usuario user = usuarioService.autenticar(login, senha);

        // Pega (ou cria) a sessao deste navegador
        HttpSession session = request.getSession();
        // Guarda o usuario logado na sessao, com a chave "usuarioLogado"
        session.setAttribute("usuarioLogado", user);

        // Deu certo: manda o navegador para o dashboard
        return "redirect:/dashboard";
    } catch (RuntimeException e) {
        // Deu erro: volta pra tela de login com "?erro" na URL
        return "redirect:/login?erro";
    }
}

    // GET /logout -> encerra a sessao atual (sai do sistema)
    @GetMapping("/logout")
public String logout(HttpServletRequest request) {

    // Pega a sessao SE ela existir (false = nao cria uma nova a toa)
    HttpSession session = request.getSession(false);

    // Se existir sessao, apaga ela inteira (derruba o login)
    if (session != null) {
        session.invalidate();
    }

    // Manda o navegador de volta para a tela de login
    return "redirect:/login";
}

    // GET /login -> so exibe a pagina de login
    @GetMapping("/login")
public String loginPage() {
    // Nome do template que o Thymeleaf vai renderizar (login.html)
    return "login";
}


}

