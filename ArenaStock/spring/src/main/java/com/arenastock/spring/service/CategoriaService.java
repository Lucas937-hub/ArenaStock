package com.arenastock.spring.service;

import com.arenastock.spring.model.Categoria;
import com.arenastock.spring.repository.CategoriaRepository;
// Marca como um "servico" gerenciado pelo Spring
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    // Dependencia do repositorio, injetada pelo Spring
    private final CategoriaRepository categoriaRepository;

    // Construtor: recebe o repositorio pronto do Spring
    public CategoriaService(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    // Regra de negocio: cadastrar uma nova categoria
    public Categoria cadastrar(Categoria categoria){

        // Se ja existe categoria com este nome, bloqueia o cadastro (evita duplicidade)
        if (categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())){
            // Interrompe a execucao com mensagem de erro
            throw new RuntimeException("Ja existe uma categoria com este nome.");
        }

        // Salva a categoria nova no banco
        return categoriaRepository.save(categoria);
    }

    // Lista todas as categorias cadastradas
    public List<Categoria> listarTodas(){
        return categoriaRepository.findAll();
    }

    // Regra de negocio: remover uma categoria pelo id
    public void remover(Long id){

        // Se o id nao existir no banco, avisa com erro em vez de tentar apagar
        if (!categoriaRepository.existsById(id)){
            throw new RuntimeException("Categoria não encontrada.");
        }

        // Apaga a categoria do banco
        categoriaRepository.deleteById(id);
    }
}
