package com.arenastock.spring.service;

import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.repository.UsuarioRepository;
// Marca a classe como um "servico" (regra de negocio), gerenciado pelo Spring
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    // Dependencia do repositorio, injetada pelo Spring via construtor
    private final UsuarioRepository usuarioRepository;

    // Construtor: o Spring cria o UsuarioService ja passando o repositorio pronto
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    // Regra de negocio: cadastrar um novo usuario
    public Usuario cadastrar(Usuario usuario){

        // Se ja existe um usuario com este login, bloqueia o cadastro
        if (usuarioRepository.existsByLogin(usuario.getLogin())){
            // Interrompe a execucao lancando um erro com mensagem amigavel
            throw new RuntimeException("Este login já está em uso.");
        }

        // Se ja existe um usuario com este e-mail, bloqueia o cadastro
        if (usuarioRepository.existsByEmail(usuario.getEmail())){
            // Interrompe a execucao lancando um erro com mensagem amigavel
            throw new RuntimeException("Este e-mail já está cadastrado.");
        }

        // Se passou pelas duas checagens, salva o usuario no banco
        return usuarioRepository.save(usuario);
    }

    // Regra de negocio: autenticar login e senha
    public Usuario autenticar(String login, String senha){

        // Busca o usuario pelo login; se nao encontrar, ja lanca o erro (mensagem generica por seguranca)
        Usuario usuario = usuarioRepository.findByLogin(login)
        .orElseThrow(() -> new RuntimeException("Login ou senha invalidos"));

        // Compara a senha digitada com a senha salva (texto puro, sem criptografia)
        if (!usuario.getSenha().equals(senha)){
            // Mesma mensagem generica de erro (nao revela se foi o login ou a senha que errou)
            throw new RuntimeException("Login ou senha invalidos");
        }

        // Se a senha bateu, devolve o usuario autenticado
        return usuario;
    }
}
