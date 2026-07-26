package com.arenastock.spring.model;

// Anotacoes do JPA
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
// Lombok
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Marca como entidade (tabela) do banco
@Entity
// Nome da tabela no MySQL
@Table(name = "logs_auditoria")
// Gera get/set/equals/hashCode/toString
@Data
// Gera construtor vazio
@NoArgsConstructor
public class LogAuditoria {

    // Chave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com o usuario responsavel pela acao.
    // "nullable = true" e SEM cascata de delete: se um dia esse usuario for
    // removido do sistema, o log NAO pode ser apagado junto (senao perderiamos
    // justamente o registro que prova quem fez o que). O Hibernate, com
    // ddl-auto=update, cria a coluna sem restricao de integridade forte aqui;
    // quem garante a rastreabilidade de verdade sao os campos de snapshot abaixo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    // "Fotografia" do nome do usuario no exato momento da acao. Guardada
    // separada do relacionamento acima de propósito: mesmo que a conta do
    // usuario seja excluida do sistema no futuro, o historico de auditoria
    // continua legivel e confiavel, mostrando quem fez o que.
    @Column(name = "usuario_nome_snapshot", nullable = false, length = 100)
    private String usuarioNomeSnapshot;

    // Mesma logica acima, mas para o login (identificador unico de acesso)
    @Column(name = "usuario_login_snapshot", nullable = false, length = 50)
    private String usuarioLoginSnapshot;

    // Qual parte do sistema foi afetada (PRODUTO, CATEGORIA ou MOVIMENTACAO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEntidadeAuditoria entidade;

    // Id do registro afetado (produto, categoria ou movimentacao). Nao e uma
    // chave estrangeira de verdade de proposito: se o registro original for
    // excluido (ex: produto apagado), este numero continua aqui como
    // referencia historica, sem travar nem ser apagado junto.
    @Column(name = "entidade_id")
    private Long entidadeId;

    // Qual acao foi realizada (CRIACAO, ATUALIZACAO ou EXCLUSAO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AcaoAuditoria acao;

    // Descricao legivel e detalhada do que aconteceu, pronta para ser exibida
    // na tela (ex.: "Produto 'Bola de Futebol Nike' excluído — tinha 12 un.")
    @Column(nullable = false, length = 500)
    private String descricao;

    // IP de onde partiu a requisicao que gerou a acao (rastreabilidade extra)
    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    // Data/hora exata da acao, preenchida automaticamente na criacao do log
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();
}