package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.exception.EstoqueInsuficienteException;
import com.bigodeautopecas.backend.exception.ResourceNotFoundException;
import com.bigodeautopecas.backend.model.ItemPedido;
import com.bigodeautopecas.backend.model.Pedido;
import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.repository.PedidoRepository;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository repo;
    private final ProdutoRepository produtoRepo;

    @Autowired(required = false)
    private EmailService emailService;

    public PedidoService(PedidoRepository repo, ProdutoRepository produtoRepo) {
        this.repo = repo;
        this.produtoRepo = produtoRepo;
    }

    public Page<Pedido> listar(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Page<Pedido> listarPorEmail(String email, Pageable pageable) {
        return repo.findByUsuarioEmail(email, pageable);
    }

    @Transactional
    public Pedido salvar(Pedido p) {
        boolean isNovo = p.getId() == null;

        if (isNovo && p.getItens() != null) {
            double total = 0.0;
            for (ItemPedido item : p.getItens()) {
                Produto produto = produtoRepo.findById(item.getProduto().getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Produto não encontrado: " + item.getProduto().getId()));

                if (produto.getEstoque() < item.getQuantidade()) {
                    throw new EstoqueInsuficienteException(
                            "Estoque insuficiente para: " + produto.getNome()
                            + " (disponível: " + produto.getEstoque()
                            + ", solicitado: " + item.getQuantidade() + ")");
                }

                item.setPrecoUnitario(produto.getPreco());
                produto.setEstoque(produto.getEstoque() - item.getQuantidade());
                produtoRepo.save(produto);
                total += produto.getPreco() * item.getQuantidade();
            }
            p.setTotal(total);
        }

        Pedido salvo = repo.save(p);

        if (isNovo && emailService != null && salvo.getUsuario() != null) {
            emailService.enviarConfirmacaoPedido(
                    salvo.getUsuario().getEmail(), salvo.getId(), salvo.getTotal());
        }

        return salvo;
    }

    public Pedido buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + id));
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repo.deleteById(id);
    }
}