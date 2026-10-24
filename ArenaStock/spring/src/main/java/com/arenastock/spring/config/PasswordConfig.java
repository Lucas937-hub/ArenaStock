package com.arenastock.spring.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// @Configuration: classe que ensina o Spring a criar objetos (beans
@Configuration
public class PasswordConfig{

    // @Bean: o Spring cria UM PasswordEncoder e o entrega a que pedir no construtor
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    } 
}