# 🏟️ ArenaStock
Sistema de gestão de estoque para produtos esportivos, desenvolvido com Java e Spring Boot. Projeto criado originalmente como trabalho de curso técnico e mantido em evolução contínua como parte de portfólio.

---

### 📌 **Sobre o projeto**

O ArenaStock nasceu de uma proposta de curso técnico: desenvolver um sistema de controle de estoque com autenticação de usuários, seguindo uma arquitetura em camadas. A proposta original previa Java Swing (aplicação desktop) — a implementa aqui foi feita em **Spring Boot**, como uma aplicação web server-side (Thymeleaf), mantendo a mesma separação de responsabilidades em camadas (Controller → Service → Repository → Model), agora usando Spring Data JPA no lugar do DAO manual.

O tema escolhido — estoque de **produtos esportivos** — foi uma decisão para já direcionar o projeto a um nicho real, mantendo a possibilidade de evoluir para um sistema usado por lojas, academias ou distribuidoras do setor esportivo.


### 🎯 **Problema que resolve**
Pequenos e médios negócios que trabalham com produtos esportivos (lojas, academias, distribuidoras) costumam controlar estoque de forma manual ou em planilhas soltas, o que gera problemas recorrentes: divergência entre o estoque real e o registrado, falta de rastreabilidade de quem alterou o quê, e dificuldade de saber rapidamente o que precisa ser reposto. O ArenaStock resolve esses pontos oferecendo: 

-  Controle centralizado de produtos, categorias e movimentações de estoque;

- Rastreabilidade completa de quem fez cada ação no sistema (auditoria);

- Controle de acesso por perfil de usuário.

### 🚀 Funcionalidades atuais

- **Autenticação de usuários** — login e cadastro, com senha e verificação de login único.
- **Perfis de acesso** — Administrador, Gerente e Estoquista, cada um com permissões diferentes dentro do sistema.
- **Cadastro de categorias** de produtos.
- **Movimentações de estoque** — registro de entrada e saída, com atualização automática da quantidade em estoque.
- **Listagem, busca, atualização e exclusão** de produtos e categorias.
- **Log de auditoria** (exclusivo para Administradores) — histórico completo de quem criou, editou ou excluir produtos, categorias e movimentações, incluindo data/hora e IP de origem. Essa funcionalidade garante transparência sobre as ações realizadas no sistema, mesmo que o usuário responsável seja removido posteriormente.
- **Interface web responsiva**, com validação de dados nos formulários.


### 🗺️ Modelo de dados

O banco de dados é composto pelas seguintes entidades principais:

- `usuarios` — dados de acesso e perfil (cargo) de cada usuário.
- `produtos` — nome, quantidade, preço, vinculado a uma categoria e a um usuário responsável.
- `categorias` — agrupamento dos produtos.
- `movimentacoes` — histórico de entradas e saídas de estoque, vinculado a um produto e a um usuário.
- `logs_auditoria` — histórico de ações (criação/atualização/exclusão) realizadas sobre produtos, categorias e movimentações, preservando quem fez cada ação mesmo que o usuario original seja excluído do sistema.

Relacionamentos principais: um usuário pode cadastrar vários produtos; um produto pertence a uma categoria; um produto pode ter várias movimentações; e cada movimentação e cada ação de auditoria são sempre atribuídas a um usuário.

### 🛠️ Tecnologias utilizadas

- **Linguagem:** Java 21
- **Framework:** Spring Boot  3.5
- **Persistência:** Spring Data JPA (Hibernate)
- **Banco de dados:** MySQL
- **Camada de visualização:** Thymeleaf(renderização server-side)
- **Outras bibliotecas:** Lombok, Spring Boot Validation
- **Build:** Maven


### 📈 Status do projeto
Este é um projeto **em evolução contínua**, iniciado como trabalho de curso técnico e mantido como parte de portfólio. As próximas melhorias planejadas (novas funcionalidades, relatórios, refinamentos de arquitetura) são documentadas na aba **Issues** deste repositório.

Contribuições e sugestões são bem-vindas via **Issues** e **Pull Requests**.

### 👥 Autores
Desenvolvido por Lucas Rodrigues, Adrian Souza e João Santos, durante o curso técnico em Desenvolvimento de Sistemas.

### 💻 Como executar o projeto

**Pré-requisitos**
- Java 21 instalado
- MySQL em execução localmente (ou acessível pela rede)
- Maven (ou usar o wrapper `mvnw` incluído no projeto)

**Passo a passo**
1. Clone o repósitorio:
```
bash

    git clone https://github.com/SEU-USUARIO/ArenaStock.git
    cd ArenaStock/spring
```
2. Crie o banco de dados no MySQL(o nome padrão configurado é `estoque_db`):
````
sql
    
    CREATE DATABASE estoque_db;
````
3. Configure o acesso ao banco em `src/main/resources/application.properties`, ajustando usuário e senha conforme o seu ambiente:
````
properties

    spring.datasources.url=jdbc:mysql:localhost:3306/estoque_db?useTimezone=t
    spring.datasource.username=root
    spirng.datasource.password=SUA_SENHA
````    
4. Executee o projeto:
````
bash

    ./mvnw spring-boot:run
````

5. Acesse no navvegador:
````
    http://localhost:8080
````

As tabelas do banco são criadas automaticamente pelo Hibernate na primeira execução (`spring.jpa.hibernate.ddl-auto=update`).

----
*Este projeto é um modelo inicial e continua sendo ativamente estudado e melhorado — sugestões e contribuições são sempre bem-vindas.*



