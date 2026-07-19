package com.arenastock.spring.service;

import com.arenastock.spring.model.Categoria;
import com.arenastock.spring.model.Produto;
import com.arenastock.spring.model.Usuario;
// Model da movimentacao e do tipo (ENTRADA/SAIDA), usados na entrada automatica
import com.arenastock.spring.model.Movimentacao;
import com.arenastock.spring.model.TipoMovimentacao;
import com.arenastock.spring.repository.CategoriaRepository;
import com.arenastock.spring.repository.ProdutoRepository;
// Repositorio de movimentacoes, para salvar a entrada automatica do cadastro
import com.arenastock.spring.repository.MovimentacaoRepository;
// Marca como um "servico" gerenciado pelo Spring
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProdutoService {

    // Dependencias dos repositorios, injetadas pelo Spring
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    // Repositorio de movimentacoes (usado para registrar a entrada automatica no cadastro)
    private final MovimentacaoRepository movimentacaoRepository;

    // Construtor: recebe os tres repositorios prontos
    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository,
                          MovimentacaoRepository movimentacaoRepository) {

        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    // Metodo auxiliar privado: busca uma categoria pelo id, ou lanca erro se nao existir
    private Categoria buscarCategoria(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Categoria não encontrada."));
    }

    // Regra de negocio: cadastrar um novo produto
    public Produto cadastrar(Produto produto,
                             Long categoriaId,
                             Usuario usuarioLogado) {

        // Busca a categoria completa pelo id recebido
        Categoria categoria = buscarCategoria(categoriaId);

        // Associa a categoria encontrada ao produto
        produto.setCategoria(categoria);

        // Associa o usuario logado como responsavel pelo cadastro (nunca vem do formulario)
        produto.setUsuario(usuarioLogado);

        // Salva o produto no banco ja com categoria e usuario definidos
        Produto produtoSalvo = produtoRepository.save(produto);

        // Se o produto ja nasceu com estoque (quantidade inicial > 0), registra
        // essa quantidade como uma movimentacao de ENTRADA. Sem isso, o estoque
        // inicial nao aparecia em nenhum lugar da tela de Movimentacoes.
        if (produtoSalvo.getQuantidade() != null && produtoSalvo.getQuantidade() > 0) {

            // Cria a movimentacao de entrada correspondente ao cadastro
            Movimentacao entradaInicial = new Movimentacao();
            entradaInicial.setProduto(produtoSalvo);
            entradaInicial.setUsuario(usuarioLogado);
            entradaInicial.setTipo(TipoMovimentacao.ENTRADA);
            entradaInicial.setQuantidade(produtoSalvo.getQuantidade());
            entradaInicial.setDataHora(LocalDateTime.now());

            // Salva a movimentacao de entrada no banco
            movimentacaoRepository.save(entradaInicial);
        }

        // Devolve o produto ja salvo
        return produtoSalvo;
    }

    // Lista todos os produtos do sistema (requisito: "listar todos os produtos")
    public List<Produto> listarTodos() {

        return produtoRepository.findAll();
    }

    // Busca produtos pelo nome (requisito: "buscar pelo nome")
    public List<Produto> buscarPorNome(String nome) {

        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Busca um produto especifico pelo id, ou lanca erro se nao existir
    public Produto buscarPorId(Long id) {

        return produtoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Produto não encontrado"));
    }

    // Regra de negocio: atualizar os dados de um produto ja existente
    public Produto atualizar(Long id, Produto dadosNovos){

        // Busca o produto original que ja esta salvo no banco
        Produto produto = buscarPorId(id);

        // Atualiza campo por campo, com os dados novos vindos do formulario
        produto.setNome(dadosNovos.getNome());
        produto.setQuantidade(dadosNovos.getQuantidade());
        produto.setPreco(dadosNovos.getPreco());

        // Busca a nova categoria escolhida, ou lanca erro se nao existir
        Categoria categoria = categoriaRepository
                .findById(dadosNovos.getCategoria().getId())
                .orElseThrow(() ->
                        new RuntimeException("Categoria não encontrada."));

        // Associa a nova categoria ao produto
        produto.setCategoria(categoria);

        // Salva o produto ja atualizado (o usuario original e a data de criacao sao preservados)
        return produtoRepository.save(produto);
    }

    // Regra de negocio: remover um produto pelo id
    public void remover(Long id) {

        // Se o id nao existir, avisa com erro em vez de tentar apagar
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado.");
        }

        // Busca todas as movimentacoes (entradas/saidas) que apontam para este produto.
        // Sem apagar elas primeiro, o banco recusa apagar o produto, porque a coluna
        // produto_id da tabela movimentacoes eh obrigatoria (chave estrangeira NOT NULL).
        List<Movimentacao> movimentacoesDoProduto =
                movimentacaoRepository.findByProdutoIdOrderByDataHoraDesc(id);

        // Apaga primeiro todo o historico de movimentacoes deste produto
        movimentacaoRepository.deleteAll(movimentacoesDoProduto);

        // Só então apaga o produto do banco
        produtoRepository.deleteById(id);
    }
}
