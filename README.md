# 🏟️ ArenaStock
Sistema de gestão de estoque para produtos esportivos, desenvolvido com Java e Spring Boot. Projeto criado originalmente como trabalho de curso técnico e mantido em evolução contínua como parte de portfólio.

---

### 📌 **Sobre o projeto**

O ArenaStock nasceu de uma proposta de curso técnico: desenvolver um sistema de controle de estoque com autenticação de usúarios, seguindo uma arquitetura em camadas. A proposta original previa Java Swing (aplicação desktop) — a implementa aqui foi feita em **Spring Boot**, como uma aplicação web server-side (Thymeleaf), mantendo a mesma separação de responsabilidades em camadas (Controller → Service → Repository → Model), agora usando Spring Data JPA no lugar do DAO manual.

O tema escolhido — estoque de **produtos esportivos** — foi uma decisão para já direcionar o projeto a um nicho real, mantendo a possibilidade de evoluir para um sistema usado por lojas, academias ou distribuidoras do setor esportivo.


### 🎯 **Problema que resolve**
Pequenos e médios negócios que trabalham com produtos esportivos (lojas, academias, distribuidoras) costumam controlar estoque de forma manual ou em planilhas soltas, o que gera problemas recorrentes: divergência entre o estoque real e o registrado, falta de rastreabilidade de quem alterou o quê, e dificuldade de saber rapidamente o que precisa ser reposto. O ArenaStock resolve esses pontos oferecendo: 

-  Controle centralizado de produtos, categorias e movimentações de estoque;

- Rastreabilidade completa de quem fez cada ação no sistema (auditoria);

- Controle de acesso por perfil de usúario.

### 🚀 Funcionalidades atuais

- **Autenticação de usúarios** — login e cadastro, com senha e verificação de login único.
- **Perfis de acesso** — Administrador, Gerente e Estoquista, cada um com permissões diferentes dentro do sistema.
- **Cadastro de categorias** de produtos.
- **Movimentações de estoque** — registro de entrada e saída, com atualização automática da quantidade em estoque.
- **Listagem, busca, atualização e exclusão** de produtos e categorias.
- **Log de auditoria** (exclusivo para Administradores) — histórico completo de quem criou, editou ou excluir produtos, categorias e movimentações, incluindo data/hora e IP de origem. Essa funcionalidade garante transparência sobre as ações realizadas no sistema, mesmo que o usúario responsável seja removido posteriormente.
- **Interface web responsiva**, com validação de dados nos formulários.



