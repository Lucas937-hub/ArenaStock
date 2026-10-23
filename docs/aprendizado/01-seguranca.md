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
