package com.arenastock.spring.model;

// Anotacoes do JPA para mapear a classe como tabela
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
// Lombok: gera getters/setters/equals/hashCode/toString
import lombok.Data;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
// Validacao: campo obrigatorio
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

// Marca como entidade (tabela) do banco
@Entity
// Nome da tabela no MySQL
@Table(name = "categorias")
// Gera get/set/equals/hashCode/toString automaticamente
@Data
// Gera construtor vazio (new Categoria())
@NoArgsConstructor
public class Categoria {

    // Chave primaria
    @Id
    // Numero gerado automaticamente pelo MySQL
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nao pode ficar vazio
    @NotBlank
    // Obrigatorio e UNICO (nao pode repetir nome de categoria)
    @Column(nullable = false, unique = true, length = 100)
    private String nome;
}
