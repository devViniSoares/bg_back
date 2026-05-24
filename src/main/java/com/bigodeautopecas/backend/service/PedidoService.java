package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.ItemPedido;
import com.bigodeautopecas.backend.model.Pedido;
import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.repository.PedidoRepository;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository repo;
    private final ProdutoRepository produtoRepo;

    public PedidoService(PedidoRepository repo, ProdutoRepository produtoRepo) {
        this.repo = repo;
        this.produtoRepo = produtoRepo;
    }

    public Page<Pedido> listar(Pageable pageable) { return repo.findAll(pageable); }

    public Page<Pedido> listarPorEmail(String email, Pageable pageable) {
        return repo.findByUsuarioEmail(email, pageable);
    }

    @Transactional
    public Pedido salvar(Pedido p) {
        if (p.getId() == null && p.getItens() != null) {
            for (ItemPedido item : p.getItens()) {
                Produto produto = produtoRepo.findById(item.getProduto().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Produto não encontrado: " + item.getProduto().getId()));
                if (produto.getEstoque() < item.getQuantidade()) {
                    throw new RuntimeException(
                            "Estoque insuficiente para o produto: " + produto.getNome()
                            + " (disponível: " + produto.getEstoque()
                            + ", solicitado: " + item.getQuantidade() + ")");
                }
                produto.setEstoque(produto.getEstoque() - item.getQuantidade());
                produtoRepo.save(produto);
            }
        }
        return repo.save(p);
    }

    public Pedido buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
    }

    public void deletar(Long id) { repo.deleteById(id); }
}