package com.arenastock.spring.repository;

import com.arenastock.spring.model.Usuario;
// Interface pronta do Spring Data JPA com metodos basicos de banco
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Estende JpaRepository<Entidade, TipoDoId>: ja ganha save/findAll/findById/deleteById/count de graca
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    // Busca um usuario pelo login (usado no login) - Optional = "pode ou nao encontrar"
    Optional<Usuario> findByLogin(String login);

    // Verifica se ja existe usuario com este login (usado no cadastro, checa duplicidade)
    boolean existsByLogin(String login);

    // Verifica se ja existe usuario com este e-mail (usado no cadastro, checa duplicidade)
    boolean existsByEmail(String email);
}

