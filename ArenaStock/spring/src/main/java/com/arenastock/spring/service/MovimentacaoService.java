package com.arenastock.spring.service;

import com.arenastock.spring.model.Movimentacao;
import com.arenastock.spring.model.Produto;
import com.arenastock.spring.model.TipoMovimentacao;
import com.arenastock.spring.model.Usuario;
import com.arenastock.spring.repository.MovimentacaoRepository;
import com.arenastock.spring.repository.ProdutoRepository;
// Marca como um "servico" gerenciado pelo Spring
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MovimentacaoService {

    // Dependencias dos repositorios, injetadas pelo Spring
    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    // Construtor: recebe os dois repositorios prontos
    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
    ProdutoRepository produtoRepository){
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    // Regra de negocio principal: registrar uma entrada ou saida e atualizar o estoque
    public Movimentacao registrar(Movimentacao movimentacao, Usuario usuarioLogado){

        // Busca o produto de verdade no banco (nao confia no que veio na requisicao)
        Produto produto = produtoRepository.findById(movimentacao.getProduto().getId())
        .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        // Se for uma SAIDA e a quantidade pedida for maior que o estoque atual, bloqueia
        if (movimentacao.getTipo() == TipoMovimentacao.SAIDA
        && produto.getQuantidade() < movimentacao.getQuantidade()){
            // Nao deixa o estoque ficar negativo
            throw new RuntimeException("Quantidade em estoque insuficiente para esta saida");
        }

        // Calcula a nova quantidade: soma se for ENTRADA, subtrai se for SAIDA
        int novaQuantidade = movimentacao.getTipo() == TipoMovimentacao.ENTRADA
            ? produto.getQuantidade() + movimentacao.getQuantidade()
            : produto.getQuantidade() - movimentacao.getQuantidade();

        // Atualiza a quantidade do produto com o novo valor calculado
        produto.setQuantidade(novaQuantidade);
        // Salva o produto ja com o estoque atualizado
        produtoRepository.save(produto);

        // Garante que a movimentacao fica associada ao produto (com dados completos)
        movimentacao.setProduto(produto);
        // Associa o usuario logado como responsavel pela movimentacao (nunca vem do formulario)
        movimentacao.setUsuario(usuarioLogado);
        // Salva a movimentacao no banco e devolve o registro salvo
        return movimentacaoRepository.save(movimentacao);

    }

    // Lista todas as movimentacoes do sistema
    public List<Movimentacao> listarTodas(){
        return movimentacaoRepository.findAll();
    }

    // Lista o historico de movimentacoes de um produto especifico
    public List<Movimentacao> listarPorProduto(Long produtoId){
        return movimentacaoRepository.findByProdutoIdOrderByDataHoraDesc(produtoId);

    }
}

