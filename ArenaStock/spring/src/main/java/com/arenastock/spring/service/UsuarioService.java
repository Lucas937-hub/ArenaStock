package com.arenastock.spring.service;

import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.repository.UsuarioRepository;
// Marca a classe como um "servico" (regra de negocio), gerenciado pelo Spring
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService {

    // Dependencia do repositorio, injetada pelo Spring via construtor
    private final UsuarioRepository usuarioRepository;
    // Ferramenta que gera e confere hashes BCrypt (entregu pelo Spring)
    private final PasswordEncoder passwordEncoder;

    // Construtor: o Spring cria o UsuarioService ja passando o repositorio pronto
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Regra de negocio: cadastrar um novo usuario
    public Usuario cadastrar(Usuario usuario) {

        // Se ja existe um usuario com este login, bloqueia o cadastro
        if (usuarioRepository.existsByLogin(usuario.getLogin())) {
            // Interrompe a execucao lancando um erro com mensagem amigavel
            throw new RuntimeException("Este login já está em uso.");
        }

        // Se ja existe um usuario com este e-mail, bloqueia o cadastro
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            // Interrompe a execucao lancando um erro com mensagem amigavel
            throw new RuntimeException("Este e-mail já está cadastrado.");
        }

        // REGRA DE SEGURANÇA: o cargo NUNCA vem do formulario.
        // o primeiro usuario do sistema vira ADMINISTRADOR; os demais, ESTOQUISTA.
        // Qualquer valor de "cargo" enviado pelo cliente e sobrescrito aqui.

        if (usuarioRepository.count() == 0) {
            usuario.setCargo("ADMINISTRADOR");
        } else {
            usuario.setCargo("ESTOQUISTA");
        }

        // Guarda apenas o hash, nunca a senha original
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    // Regra de negocio: autenticar login e senha
    public Usuario autenticar(String login, String senha) {

        // Busca o usuario pelo login; se nao encontrar, ja lanca o erro (mensagem
        // generica por seguranca)
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Login ou senha invalidos"));

        // matches() aplica o hash na senha digitada e compara com o hash salvo
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Login ou senha invalidos");
        }

        // Se a senha bateu, devolve o usuario autenticado
        return usuario;
    }
}
