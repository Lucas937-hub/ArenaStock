package com.arenastock.spring.config;

import com.arenastock.spring.model.Categoria;
import com.arenastock.spring.repository.CategoriaRepository;
// "Receita" que ensina o Spring a converter um tipo em outro
import org.springframework.core.convert.converter.Converter;
// Lugar onde se registram conversores customizados
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
// Avisa o Spring pra criar esta classe automaticamente ao iniciar
import org.springframework.stereotype.Component;
// Interface que permite plugar configuracoes extras no Spring MVC
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Sem este conversor, o formulario de produtos (que envia apenas o ID
// da categoria selecionada no <select>) nao consegue ser convertido
// automaticamente para o objeto Categoria esperado por Produto,
// e o cadastro/edicao de produto pela pagina falha.
@Component
public class WebConfig implements WebMvcConfigurer {

    // Dependencia do repositorio, injetada pelo Spring
    private final CategoriaRepository categoriaRepository;

    // Construtor: recebe o repositorio pronto
    public WebConfig(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Metodo que registra o conversor customizado no Spring MVC
    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        // Ensina o Spring: "toda vez que precisar converter String -> Categoria, use isto"
        registry.addConverter(new Converter<String, Categoria>() {
            @Override
            public Categoria convert(@NonNull String source) {
                // Texto vazio -> nao ha categoria pra converter
                if (source.isBlank()) {
                    return null;
                }
                // Transforma o texto em Long e busca a categoria correspondente no banco
                return categoriaRepository.findById(Long.valueOf(source)).orElse(null);
            }
        });
    }
}
