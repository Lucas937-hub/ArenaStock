package com.arenastock.spring.service;

import com.arenastock.spring.model.AcaoAuditoria;
import com.arenastock.spring.model.LogAuditoria;
import com.arenastock.spring.model.TipoEntidadeAuditoria;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.repository.LogAuditoriaRepository;
// Marca como um "servico" gerenciado pelo Spring
import org.springframework.stereotype.Service;

import java.util.List;

// Service central de auditoria. Os demais services (Produto, Categoria,
// Movimentacao) chamam este aqui sempre que uma acao relevante acontece,
// pra deixar registrado quem fez o que, quando e de onde.
@Service
public class AuditoriaService {

    // Dependencia do repositorio, injetada pelo Spring
    private final LogAuditoriaRepository logAuditoriaRepository;

    // Construtor: recebe o repositorio pronto
    public AuditoriaService(LogAuditoriaRepository logAuditoriaRepository) {
        this.logAuditoriaRepository = logAuditoriaRepository;
    }

    // Registra uma nova entrada no log de auditoria.
    // usuarioResponsavel: quem estava logado e realizou a acao (nunca nulo,
    //   pois todas as acoes auditadas exigem login).
    // entidade/entidadeId: o que foi afetado (ex: PRODUTO, id 7).
    // acao: CRIACAO, ATUALIZACAO ou EXCLUSAO.
    // descricao: texto pronto pra exibir na tela, explicando o que mudou.
    // ipOrigem: endereco de onde partiu a requisicao (pode ser nulo).
    public void registrar(Usuario usuarioResponsavel,
                          TipoEntidadeAuditoria entidade,
                          Long entidadeId,
                          AcaoAuditoria acao,
                          String descricao,
                          String ipOrigem) {

        LogAuditoria log = new LogAuditoria();

        // Guarda o vinculo "vivo" com o usuario...
        log.setUsuario(usuarioResponsavel);
        // ...e tambem a "fotografia" do nome/login no momento da acao, para
        // que o registro nunca fique orfao mesmo se o usuario for removido depois
        log.setUsuarioNomeSnapshot(usuarioResponsavel.getNome());
        log.setUsuarioLoginSnapshot(usuarioResponsavel.getLogin());

        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setAcao(acao);
        log.setDescricao(descricao);
        log.setIpOrigem(ipOrigem);

        logAuditoriaRepository.save(log);
    }

    // Lista todo o historico de auditoria, do mais recente pro mais antigo
    public List<LogAuditoria> listarTodos() {
        return logAuditoriaRepository.findAllByOrderByDataHoraDesc();
    }

    // Lista o historico de acoes de um usuario especifico
    public List<LogAuditoria> listarPorUsuario(Long usuarioId) {
        return logAuditoriaRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId);
    }

    // Lista o historico de acoes sobre um tipo de entidade especifico
    public List<LogAuditoria> listarPorEntidade(TipoEntidadeAuditoria entidade) {
        return logAuditoriaRepository.findByEntidadeOrderByDataHoraDesc(entidade);
    }
}

