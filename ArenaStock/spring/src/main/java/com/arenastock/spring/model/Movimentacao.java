package com.arenastock.spring.model;

// Anotacoes do JPA
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
// Lombok
import lombok.Data;
import java.time.LocalDateTime;
// Relacionamento "muitos para um"
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
// Para salvar o enum como texto no banco
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
// Validacoes
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NoArgsConstructor;

// Marca como entidade (tabela)
@Entity
// Nome da tabela no MySQL
@Table(name = "movimentacoes")
// Gera get/set/equals/hashCode/toString
@Data
// Gera construtor vazio
@NoArgsConstructor
public class Movimentacao {

    // Chave primaria
    @Id
    // Gerado automaticamente pelo MySQL
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento: muitas movimentacoes para um produto
    // LAZY = so busca o produto do banco quando for realmente pedido (economiza consulta)
    @ManyToOne(fetch = FetchType.LAZY)
    // Coluna de chave estrangeira que aponta para produtos.id
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Relacionamento: muitas movimentacoes para um usuario responsavel
    @ManyToOne(fetch = FetchType.LAZY)
    // Coluna de chave estrangeira que aponta para usuarios.id
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Tipo nao pode ser nulo
    @NotNull
    // Salva o enum como texto ("ENTRADA"/"SAIDA"), nao como numero
    @Enumerated(EnumType.STRING)
    // Obrigatorio no banco
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    // Quantidade nao pode ser nula
    @NotNull
    // Quantidade tem que ser maior que zero
    @Positive
    // Obrigatoria no banco
    @Column(nullable = false)
    private Integer quantidade;

    // Data/hora da movimentacao, preenchida automaticamente na criacao do objeto
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();
}
