package com.arenastock.spring.service;

import com.arenastock.spring.model.AcaoAuditoria;
import com.arenastock.spring.model.Categoria;
import com.arenastock.spring.model.TipoEntidadeAuditoria;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.repository.CategoriaRepository;
// Marca como um "servico" gerenciado pelo Spring
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    // Dependencia do repositorio, injetada pelo Spring
    private final CategoriaRepository categoriaRepository;
    // Service de auditoria (usado para registrar quem criou/excluiu cada categoria)
    private final AuditoriaService auditoriaService;

    // Construtor: recebe o repositorio e o service de auditoria prontos
    public CategoriaService(CategoriaRepository categoriaRepository,
                            AuditoriaService auditoriaService){
        this.categoriaRepository = categoriaRepository;
        this.auditoriaService = auditoriaService;
    }

    // Regra de negocio: cadastrar uma nova categoria
    // usuarioLogado/ipOrigem: quem fez o cadastro, usado no log de auditoria
    public Categoria cadastrar(Categoria categoria, Usuario usuarioLogado, String ipOrigem){

        // Se ja existe categoria com este nome, bloqueia o cadastro (evita duplicidade)
        if (categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())){
            // Interrompe a execucao com mensagem de erro
            throw new RuntimeException("Ja existe uma categoria com este nome.");
        }

        // Salva a categoria nova no banco
        Categoria categoriaSalva = categoriaRepository.save(categoria);

        // Registra no log de auditoria quem criou esta categoria
        auditoriaService.registrar(
                usuarioLogado,
                TipoEntidadeAuditoria.CATEGORIA,
                categoriaSalva.getId(),
                AcaoAuditoria.CRIACAO,
                "Categoria '" + categoriaSalva.getNome() + "' cadastrada.",
                ipOrigem
        );

        return categoriaSalva;
    }

    // Lista todas as categorias cadastradas
    public List<Categoria> listarTodas(){
        return categoriaRepository.findAll();
    }

    // Regra de negocio: remover uma categoria pelo id
    // usuarioLogado/ipOrigem: quem fez a exclusao, usado no log de auditoria
    public void remover(Long id, Usuario usuarioLogado, String ipOrigem){

        // Busca a categoria ANTES de apagar, pra guardar o nome dela no log
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        // Apaga a categoria do banco
        categoriaRepository.deleteById(id);

        // Registra no log de auditoria quem excluiu esta categoria
        auditoriaService.registrar(
                usuarioLogado,
                TipoEntidadeAuditoria.CATEGORIA,
                id,
                AcaoAuditoria.EXCLUSAO,
                "Categoria '" + categoria.getNome() + "' excluída.",
                ipOrigem
        );
    }
}

