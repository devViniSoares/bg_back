package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.dto.ItemPedidoDTO;
import com.bigodeautopecas.backend.dto.PedidoDTO;
import com.bigodeautopecas.backend.dto.ProdutoResumoDTO;
import com.bigodeautopecas.backend.dto.UsuarioResumoDTO;
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

    // ── Leitura (retornam DTO) ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<PedidoDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PedidoDTO> listarPorEmail(String email, Pageable pageable) {
        return repo.findByUsuarioEmail(email, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PedidoDTO buscarPorIdComoDTO(Long id) {
        return toDTO(buscarPorId(id));
    }

    // ── Escrita ───────────────────────────────────────────────────────────────

    @Transactional
    public PedidoDTO salvarComoDTO(Pedido p) {
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

        return toDTO(salvo);
    }

    @Transactional
    public PedidoDTO atualizarStatus(Long id, String status) {
        Pedido pedido = buscarPorId(id);
        pedido.setStatus(status);
        return toDTO(repo.save(pedido));
    }

    // ── Uso interno ──────────────────────────────────────────────────────────

    /** Retorna entidade para uso interno (verificações, pagamento). */
    public Pedido buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + id));
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repo.deleteById(id);
    }

    // ── Mapeamento ───────────────────────────────────────────────────────────

    /** Deve ser chamado dentro de um contexto @Transactional para que o lazy loading funcione. */
    private PedidoDTO toDTO(Pedido p) {
        UsuarioResumoDTO usuario = null;
        if (p.getUsuario() != null) {
            usuario = new UsuarioResumoDTO(
                    p.getUsuario().getId(),
                    p.getUsuario().getNome(),
                    p.getUsuario().getEmail()
            );
        }

        var itens = p.getItens().stream()
                .map(i -> new ItemPedidoDTO(
                        i.getId(),
                        i.getQuantidade(),
                        i.getPrecoUnitario(),
                        new ProdutoResumoDTO(
                                i.getProduto().getId(),
                                i.getProduto().getNome(),
                                i.getProduto().getPreco(),
                                i.getProduto().getImagemUrl(),
                                i.getProduto().getCategoria(),
                                i.getProduto().getMarca()
                        )
                )).toList();

        return new PedidoDTO(
                p.getId(),
                p.getTotal(),
                p.getStatus(),
                usuario,
                p.getEnderecoEntrega(),
                itens,
                p.getCriadoEm(),
                p.getAtualizadoEm()
        );
    }
}
