package com.arenastock.spring.repository;

import com.arenastock.spring.model.Categoria;
// Interface pronta do Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Ja ganha save/findAll/findById/deleteById/count automaticamente
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

    // Busca categoria pelo nome, ignorando maiuscula/minuscula
    Optional<Categoria> findByNomeIgnoreCase(String nome);

    // Verifica se ja existe categoria com este nome (evita duplicar)
    boolean existsByNomeIgnoreCase(String nome);

    // Busca categoria pelo id (o JpaRepository ja teria isso, redeclarado aqui)
    Optional<Categoria> findById(Long id);
}
