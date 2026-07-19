package com.arenastock.spring.config;

import com.arenastock.spring.model.Categoria;
import com.arenastock.spring.repository.CategoriaRepository;

// Interface especial: o metodo run() executa sozinho quando o sistema termina de subir
import org.springframework.boot.CommandLineRunner;
// Avisa o Spring pra criar esta classe automaticamente ao iniciar
import org.springframework.stereotype.Component;

import java.util.List;

// Garante que as 5 categorias esportivas existam no banco assim que o
// sistema iniciar. Cada categoria só é criada se ainda não existir uma
// com o mesmo nome, então não duplica nada e não apaga categorias que
// você já tenha cadastrado manualmente (mesmo as que não são de esporte).
@Component
public class DataInitializer implements CommandLineRunner {

    // Dependencia do repositorio, injetada pelo Spring
    private final CategoriaRepository categoriaRepository;

    // Construtor: recebe o repositorio pronto
    public DataInitializer(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Executado automaticamente uma vez, assim que o sistema sobe
    @Override
    public void run(String... args) {

        // Lista com os nomes das categorias que devem existir
        List<String> categoriasIniciais = List.of(
                "Futebol",
                "Basquete",
                "Vôlei",
                "Luta",
                "Natação"
        );

        // Para cada nome da lista...
        for (String nome : categoriasIniciais) {

            // Se ja existe categoria com este nome, pula pra proxima (nao duplica)
            if (categoriaRepository.existsByNomeIgnoreCase(nome)) {
                continue;
            }

            // Cria uma categoria nova com o nome atual
            Categoria categoria = new Categoria();
            categoria.setNome(nome);
            // Salva a categoria no banco
            categoriaRepository.save(categoria);
        }
    }
}
