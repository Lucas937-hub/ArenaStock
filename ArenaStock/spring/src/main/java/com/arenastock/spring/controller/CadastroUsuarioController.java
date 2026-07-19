package com.arenastock.spring.controller;

// Marca como um controller de pagina
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.arenastock.spring.model.Usuario;

@Controller
public class CadastroUsuarioController {


    // GET /cadastro-usuario -> so exibe a tela de cadastro
    @GetMapping("/cadastro-usuario")
public String cadastroUsuario(Model model) {

    // Cria um Usuario vazio pro Thymeleaf usar de "molde" no formulario
    model.addAttribute("usuario", new Usuario());

    // Nome do template que sera renderizado (cadastro-usuario.html)
    return "cadastro-usuario";
}
}

