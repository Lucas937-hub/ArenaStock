package com.arenastock.spring;

// Classe que liga a configuracao automatica do Spring Boot
import org.springframework.boot.SpringApplication;
// Anotacao que ativa toda a magica do Spring Boot (auto-config + varredura de pacotes)
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Marca esta classe como o ponto de partida de todo o sistema Spring Boot
@SpringBootApplication
public class Application {

    // Metodo main: é o primeiro codigo executado quando o programa roda
    public static void main(String[] args) {
        // Sobe o servidor embutido (Tomcat), cria os beans e deixa o sistema esperando requisicoes
        SpringApplication.run(Application.class, args);
    }

}
