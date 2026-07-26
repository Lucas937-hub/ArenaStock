package com.arenastock.spring.repository;

import com.arenastock.spring.model.LogAuditoria;
import com.arenastock.spring.model.TipoEntidadeAuditoria;
// Interface pronta do Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Ja ganha save/findAll/findById/deleteById/count automaticamente
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    // Todos os registros de auditoria, do mais recente pro mais antigo
    // -> usado para montar a tabela completa da tela de Auditoria
    List<LogAuditoria> findAllByOrderByDataHoraDesc();

    // Historico de acoes realizadas por um usuario especifico, mais recente primeiro
    // -> util para investigar tudo que uma pessoa fez no sistema
    List<LogAuditoria> findByUsuarioIdOrderByDataHoraDesc(Long usuarioId);

    // Historico de acoes sobre um tipo de entidade especifico (ex: so PRODUTO)
    List<LogAuditoria> findByEntidadeOrderByDataHoraDesc(TipoEntidadeAuditoria entidade);
}
