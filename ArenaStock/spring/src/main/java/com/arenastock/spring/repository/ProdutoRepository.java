package com.arenastock.spring.repository;

import java.util.List;

// Interface pronta do Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;

import com.arenastock.spring.model.Produto;

// Ja ganha save/findAll/findById/deleteById/count automaticamente
public interface ProdutoRepository extends JpaRepository<Produto, Long>{

    // Busca produtos cujo nome contem o texto informado (sem diferenciar maiuscula/minuscula)
    // -> usado no requisito "buscar produto pelo nome"
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Busca todos os produtos cadastrados por um usuario especifico
    List<Produto> findByUsuarioId(Long usuarioId);

    // Busca produtos com quantidade menor ou igual ao valor informado, do menor pro maior
    // -> usado no dashboard para mostrar "produtos com estoque baixo"
    List<Produto> findByQuantidadeLessThanEqualOrderByQuantidadeAsc(Integer quantidade);

    // Conta quantos produtos existem numa categoria (metodo adicionado)
    // -> usado na tela de Categorias para mostrar quantos produtos usam cada categoria
    long countByCategoriaId(Long categoriaId);
}
