package com.arenastock.spring.model;

// Anotacoes do JPA/Hibernate para mapear a classe numa tabela
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// Anotacoes de validacao (Bean Validation)
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
// Lombok: gera getters/setters/equals/hashCode/toString sozinho
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Diz ao Hibernate: esta classe representa uma tabela do banco
@Entity
// Nome exato da tabela no MySQL
@Table(name = "usuarios")
// Lombok gera get/set/equals/hashCode/toString para todos os campos
@Data
// Lombok gera um construtor sem parametros (new Usuario())
@NoArgsConstructor
public class Usuario {

    // Chave primaria da tabela
    @Id
    // O proprio MySQL gera o numero (AUTO_INCREMENT)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nao pode ficar vazio/nulo, com mensagem de erro customizada
    @NotBlank(message = "O nome é obrigatório.")
    // Coluna obrigatoria no banco, tamanho maximo 100 caracteres
    @Column(nullable = false, length = 100)
    private String nome;

    // Nao pode ficar vazio
    @NotBlank(message = "O login é obrigatório.")
    // Obrigatorio e UNICO no banco (nao pode repetir entre usuarios)
    @Column(nullable = false, unique = true, length = 50)
    private String login;

    // Nao pode ficar vazio
    @NotBlank(message = "O e-mail é obrigatório.")
    // Valida se o texto tem formato de e-mail valido
    @Email(message = "Informe um e-mail válido.")
    // Obrigatorio e UNICO no banco
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    // Nao pode ficar vazia
    @NotBlank(message = "A senha é obrigatória.")
    // Obrigatoria no banco (guardada em texto puro neste projeto)
    @Column(nullable = false, length = 100)
    private String senha;

    // Nao pode ficar vazio
    @NotBlank(message = "O cargo é obrigatório.")
    // Obrigatorio no banco
    @Column(nullable = false, length = 30)
    private String cargo;

    // Data de criacao, preenchida automaticamente no momento em que o objeto é criado
    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
