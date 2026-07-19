package com.arenastock.spring.repository;
// Interface pronta do Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;
import com.arenastock.spring.model.Movimentacao;
import java.util.List;

// Ja ganha save/findAll/findById/deleteById/count automaticamente
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long>{

    // Historico de movimentacoes de um produto especifico, mais recente primeiro
    List<Movimentacao> findByProdutoIdOrderByDataHoraDesc(Long produtoId);

    // Historico de movimentacoes feitas por um usuario especifico, mais recente primeiro
    List<Movimentacao> findByUsuarioIdOrderByDataHoraDesc(Long usuarioId);

    // As 5 movimentacoes mais recentes de todo o sistema (usado no dashboard)
    List<Movimentacao> findTop5ByOrderByDataHoraDesc();

    // Todas as movimentacoes, da mais recente pra mais antiga (metodo adicionado)
    // -> usado para montar a tabela completa da tela de Movimentacoes
    List<Movimentacao> findAllByOrderByDataHoraDesc();
}
