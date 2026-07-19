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
// Tipo usado para valores em dinheiro (sem perda de precisao)
import java.math.BigDecimal;
import java.time.LocalDateTime;
// Validacoes
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
// Relacionamento "muitos para um"
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.NoArgsConstructor;

// Marca como entidade (tabela)
@Entity
// Nome da tabela no MySQL
@Table(name = "produtos")
// Gera get/set/equals/hashCode/toString
@Data
// Gera construtor vazio
@NoArgsConstructor
public class Produto {

    // Chave primaria
    @Id
    // Gerado automaticamente pelo MySQL
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome nao pode ficar vazio
    @NotBlank
    // Obrigatorio, ate 150 caracteres
    @Column(nullable = false, length = 150)
    private String nome;

    // Quantidade nao pode ser nula
    @NotNull
    // Quantidade tem que ser zero ou positiva (nunca negativa)
    @PositiveOrZero
    // Obrigatoria no banco
    @Column(nullable = false)
    private Integer quantidade;

    // Preco nao pode ser nulo
    @NotNull
    // Preco tem que ser maior que zero
    @Positive
    // Obrigatorio, com precisao de 10 digitos e 2 casas decimais
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    // Relacionamento: muitos produtos para uma categoria
    @ManyToOne(fetch = FetchType.EAGER)
    // Coluna de chave estrangeira que aponta para categorias.id
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Relacionamento: muitos produtos para um usuario responsavel
    @ManyToOne(fetch = FetchType.EAGER)
    // Coluna de chave estrangeira que aponta para usuarios.id
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Data de cadastro, preenchida automaticamente na criacao do objeto
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

}
