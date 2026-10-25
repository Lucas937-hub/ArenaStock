# Bloco 1: Segurança (Issue #1)

> Registro de aprendizado do ArenaStock. Cada passo documenta o problema,
> a causa, a correção e como foi testado, para que a história do projeto
> possa ser lida (e entendida) por qualquer pessoa, inclusive eu no futuro.

## Como estou trabalhando

- **Branch:** `feature/seguranca`. A `main` fica sempre funcionando; cada melhoria
  nasce em uma branch e só entra na `main` quando está pronta.
- **Issue #1:** registra o *problema* e o plano antes do código. É o "porquê"
  por trás dos commits.
- **Commits pequenos:** um commit resolve um problema só, e a mensagem explica
  o que mudou e por quê.
- **`Refs #1` x `Closes #1`:** `Refs` só cria um link entre o commit e a Issue.
  `Closes` fecha a Issue quando o código chega à `main`, então só é usado no final.

---

## Passo 1: Ocultar a senha nas respostas da API

### 1.1 O problema (o "antes")

**Como reproduzi:**

1. Subi o projeto (`mvnw spring-boot:run`) com o MySQL ligado.
2. Cadastrei um usuário em `/cadastro-usuario` com a senha de teste `teste123`.
3. Fiz login em `/login`.
4. Cadastrei um produto na tela de Produtos.
5. Com o login ativo, abri no navegador `http://localhost:8080/api/produtos`.

**O que vi:** o JSON devolvido pela API trazia, dentro do campo `"usuario"`
de cada produto, o campo `"senha"` com o valor `teste123` completamente legível:

```json
"usuario": {
    "nome": "...",
    "login": "...",
    "senha": "teste123"
}
```

**Por que é grave:** qualquer pessoa logada, de qualquer cargo, que abrisse essa
URL veria a senha de quem cadastrou cada produto. Como muita gente reutiliza
senhas, o vazamento ultrapassa o sistema.

### 1.2 A causa

O vazamento é uma cadeia de três fatos:

1. **`Produto` tem um `Usuario` dentro dele** (o responsável pelo cadastro),
   carregado de forma `EAGER`, ou seja, sempre junto com o produto.
2. **`Usuario` tem o campo `senha`**, e a entidade não dizia nada sobre escondê-lo.
3. **O Spring converte objetos Java em JSON usando a biblioteca Jackson**, e o
   comportamento padrão do Jackson é incluir **todos** os campos do objeto.

Resultado: ao devolver um `Produto`, o Jackson percorre o objeto inteiro, entra
no `Usuario` e escreve a `senha` no JSON.

O mesmo acontece em qualquer endpoint que devolva um objeto que contenha um
`Usuario` (movimentações, auditoria etc.), não só em `/api/produtos`.

### 1.3 A correção

Arquivo: `model/Usuario.java`

```java
import com.fasterxml.jackson.annotation.JsonProperty;

    @NotBlank(message = "A senha é obrigatória.")
    // WRITE_ONLY: o Jackson aceita este campo ao LER um JSON de entrada,
    // mas nunca o inclui ao ESCREVER um JSON de saída.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String senha;
```

**O que cada parte faz:**

| Peça | Função |
|---|---|
| Jackson | Biblioteca que converte objetos Java em JSON e o contrário |
| `@JsonProperty` | Anotação que controla como um campo aparece no JSON |
| `Access.WRITE_ONLY` | O campo só *entra* (JSON de entrada) e nunca *sai* (JSON de saída) |

**Por que na entidade e não no controller:** a regra fica junto do dado
sensível. Assim ela vale para todos os endpoints atuais e futuros, sem depender
de lembrar de proteger cada um.

### 1.4 Como testei (o "depois")

- [x] Reiniciei o servidor e abri `/api/produtos` de novo: o campo `"senha"` não aparece mais.
- [x] Fiz logout e login novamente: continua funcionando (o login usa formulário HTML, que não passa pelo Jackson).
- [x] Cadastrei um produto novo pela tela: continua funcionando.

### 1.5 Limites desta correção

Isto esconde a senha na **saída** da API, mas **não protege a senha em si**:

- No banco, a coluna `senha` continua com o texto puro (`SELECT login, senha FROM usuarios;` mostra `teste123`).
- Quem tiver acesso ao banco ou a um backup vê todas as senhas.

Isso é resolvido no **Passo 2** (hash com BCrypt). São dois commits separados
porque são dois problemas diferentes, com duas causas e duas correções.

### 1.6 O que aprendi

- Objetos que saem da API devem ser pensados como uma "vitrine": nem tudo que
  existe na entidade deve aparecer nela.
- Entidades JPA usadas direto como resposta HTTP são arriscadas, porque qualquer
  campo novo passa a vazar automaticamente. A solução mais robusta (a longo
  prazo) é usar DTOs, objetos próprios para a resposta.
- Reproduzir o problema antes de corrigir prova que a falha existe e prova que
  a correção funcionou.

---

## Passo 2: Senhas com hash BCrypt

### 2.1 O problema (o "antes")

Rodei no banco:

```sql
SELECT login, senha FROM usuarios;
```

**O que vi:** a senha de cada usuário gravada exatamente como foi digitada
No meu teste, a senha de teste apareceu como `78901`, legível para qualquer
pessoa com acesso ao banco.

**Por que é grave:** quem conseguir ler o banco (backup vazado, invasão,
acesso indevido) sabe a senha de todos. Como muita gente reutiliza senhas,
o dano ultrapassa o sistema. O Passo 1 escondeu a senha na API, mas não
protegeu o dado em si.

### 2.2 O conceito

- **Hash:** função de mão única. Transforma `teste123` em uma sequência como
  `$2a$10$...`. Não existe caminho de volta.
- **Como o login funciona então:** aplica-se o mesmo algoritmo à senha digitada
  e **compara-se hash com hash**.
- **BCrypt** foi feito para senhas por dois motivos:
  - é **lento de propósito**, o que encarece ataques de tentativa e erro;
  - usa **salt** (valor aleatório embutido no hash), então a mesma senha gera
    hashes diferentes para pessoas diferentes.

### 2.3 O que mudei

| Arquivo | Mudança |
|---|---|
| `pom.xml` | Dependência `spring-security-crypto` (só as ferramentas de criptografia; o Spring Security completo vem no Passo 4) |
| `config/PasswordConfig.java` | Novo. Declara um `@Bean` do tipo `PasswordEncoder` (BCrypt) |
| `service/UsuarioService.java` | `cadastrar()` usa `encode()`; `autenticar()` usa `matches()` |

**Conceitos usados:**

- **`@Configuration` + `@Bean`:** ensinam o Spring a criar um objeto uma única
  vez e entregá-lo onde for pedido.
- **Injeção de dependência:** o `UsuarioService` recebe o `PasswordEncoder` pelo
  construtor, sem `new` espalhado pelo código. Trocar o algoritmo no futuro
  exige mudar um lugar só.

### 2.4 Erros que apareceram (e o que ensinaram)

- **`package org.springframework.context.security.crypto.bcrypt does not exist`**
  O `import` tinha a palavra `context` a mais. O Java compara o caminho do
  pacote letra por letra. A mensagem do Maven já aponta arquivo, linha e coluna
  (`PasswordConfig.java:[6,58]`).
- **`git add` com caminho errado** (`pathspec ... did not match any files`)
  Eu estava na subpasta `spring`, e o Git interpreta caminhos a partir da pasta
  atual. Regra: antes de cada `git add`, olhar o prompt e usar o caminho que o
  próprio `git status` mostra.
- **`git add .` é arriscado** num repositório público: pode levar senhas e
  arquivos locais. Adicionar arquivo por arquivo e conferir com `git status`.

### 2.5 Como testei (o "depois")

- [ ] Recriei o banco (`DROP DATABASE` / `CREATE DATABASE`) e o Hibernate
      recriou as tabelas sozinho (`ddl-auto=update`).
- [ ] Cadastrei um usuário e fiz login: funcionou.
- [ ] `SELECT login, senha FROM usuarios;` mostrou `$2a$10$...` com 60 caracteres.
- [ ] Dois usuários com a mesma senha geraram hashes diferentes (salt).
- [ ] Login com senha errada falhou.

### 2.6 Limites e próximos passos

- Usuários antigos (com senha em texto puro) deixam de conseguir entrar. Em um
  sistema com dados reais seria preciso uma migração gradual: aceitar a senha
  antiga uma vez, gerar o hash e regravar.
- Ainda falta controlar **quem pode escolher o cargo** (Passo 3) e **exigir
  login nas rotas** (Passo 4).

### 2.7 O que aprendi

- Nunca se guarda a senha original, só o hash.
- Salt faz a mesma senha gerar hashes diferentes.
- Ler a mensagem de erro completa (arquivo, linha, coluna) resolve a maioria
  dos problemas sem tentativa e erro.

---

## Passo 3: O cargo não pode ser escolhido no cadastro público

### 3.1 O problema (o "antes")

A tela `/cadastro-usuario` tinha um campo **Cargo** com as opções
Administrador, Gerente e Estoquista, e o valor escolhido pelo visitante era
gravado no banco.

**Como reproduzi:** cadastrei o usuário `invasor` escolhendo Administrador,
fiz login e consegui acessar a Auditoria.

**Como o problema se manifestava:** o formulário oferecia o campo Cargo com a
opção Administrador, e o valor escolhido era gravado no banco. Um usuário que
cadastrei antes da correção, escolhendo esse cargo, ficou como ADMINISTRADOR.
O commit `define o cargo no servidor e remove a escolha de cargo do cadastro` remove o campo da tela.

> Não salvei uma captura no momento do teste. O resultado da seção 3.4 foi
> capturado depois da correção e inclui, na mesma tabela, o usuário criado antes.

**Por que é grave:** qualquer visitante vira administrador em três cliques.
O sistema confia no cargo gravado no banco, então um cargo falso passa por
verdadeiro. Nenhuma permissão futura faria sentido com isso aberto.

### 3.2 A causa e a lição principal

O cargo vinha do formulário, ou seja, **de quem estava fazendo o pedido**.

> **Nunca confie nos dados que vêm do cliente.** O navegador é território do
> usuário; o servidor é o único lugar onde as regras de segurança valem.

Esconder o campo na tela não resolve sozinho. O formulário é só uma
conveniência: quem conhece HTTP monta o pedido à mão, incluindo um
`cargo=ADMINISTRADOR` que o formulário nem tem. A proteção precisa estar no
servidor.

### 3.3 A correção (duas partes)

1. **Tela:** removi o bloco do campo Cargo em `cadastro-usuario.html`.
2. **Servidor:** em `UsuarioService.cadastrar()`, o cargo é definido pelo
   próprio sistema e **sobrescreve** qualquer valor recebido:
   - se `usuarioRepository.count() == 0` (primeiro usuário), vira `ADMINISTRADOR`;
   - caso contrário, vira `ESTOQUISTA` (o cargo de menor poder).

A regra fica no **service** (regra de negócio) e não no controller, para valer
por qualquer caminho que chame `cadastrar()`. Princípio aplicado: **menor
privilégio**, ou seja, todo mundo começa com o mínimo e só ganha poder por
decisão de quem já o tem.

### 3.4 Como testei (o "depois")

- [x] A tela de cadastro não mostra mais o campo Cargo.
- [x] Cadastrei um usuário comum: `SELECT login, cargo FROM usuarios;` mostrou `ESTOQUISTA`.
- [x] Esse usuário não viu o menu Auditoria.
- [x] **O ataque:** simulei quem ignora a tela, enviando o pedido direto:

```
curl.exe -X POST http://localhost:8080/cadastro-usuario -d "nome=Hacker&email=h@h.com&login=hacker&senha=12345678&cargo=ADMINISTRADOR"

**Resultado no banco** (`SELECT login, cargo FROM usuarios;`):

```
login        cargo
----------   -------------
admin_antigo   ADMINISTRADOR   <- cadastrado ANTES da correção, escolhendo o cargo
usuario_novo    ESTOQUISTA      <- cadastrado DEPOIS, pelo formulário
hacker       ESTOQUISTA      <- enviado via curl pedindo ADMINISTRADOR
```

A mesma consulta mostra os dois momentos: o cargo escolhido livremente antes da
correção e o cargo definido pelo servidor depois, inclusive quando o pedido
tentou forçar ADMINISTRADOR.
```

O usuário `hacker` apareceu no banco como `ESTOQUISTA`, mesmo pedindo
`ADMINISTRADOR`. Esse teste prova que a proteção está no servidor e não na tela.
O `curl` não imprime nada porque o servidor responde com um redirecionamento
sem corpo; a prova está no banco.

### 3.5 Limites da solução (honestidade técnica)

- **"O primeiro usuário vira administrador" serve para desenvolvimento.** Num
  sistema publicado, o primeiro visitante poderia tomar o controle. No deploy,
  isso será trocado por um administrador criado de forma controlada (por
  exemplo, por variável de ambiente).
- **Usuários antigos não mudam.** Quem já estava no banco mantém o cargo
  antigo e precisa ser corrigido manualmente:
  `UPDATE usuarios SET cargo = 'ESTOQUISTA' WHERE login = '...';`
- **Não existe tela para promover usuários.** Por enquanto a promoção é feita
  direto no banco. Uma tela de gestão de usuários (só para administradores) é
  uma melhoria futura.
- **O campo `cargo` ainda é um texto livre** (`String`). Um `enum` (`Cargo`)
  impediria valores inválidos e erros de digitação.

### 3.6 Lição sobre o Git neste passo

O commit não subiu na primeira tentativa. Sequência de sinais que mostra isso:
`pathspec ... did not match any files` + `Changes not staged for commit` +
`Everything up-to-date`. A causa foi rodar o `git add` dentro da subpasta
`spring`, e o Git lê os caminhos a partir da pasta atual. Regra: **um terminal
para o servidor (pasta `spring`) e outro para o Git (raiz do repositório).**

Também notei que o VS Code reformatou o `UsuarioService.java` inteiro ao salvar
(`){` virou `) {`), misturando estilo com lógica no mesmo diff. Vale ler o
`git diff` antes de cada commit e, se necessário, desligar o *Format On Save*.

### 3.7 O que aprendi

- Segurança de verdade acontece no servidor; a tela é só interface.
- Princípio do menor privilégio: o padrão deve ser o mínimo de poder.
- Provar a correção atacando o sistema do mesmo jeito que um invasor atacaria
  (o teste do `curl`) vale mais que só olhar a tela.
- Conhecer e registrar os limites da própria solução faz parte do trabalho.
