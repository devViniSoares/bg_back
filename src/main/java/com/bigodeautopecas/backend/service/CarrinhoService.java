package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.dto.CarrinhoDTO;
import com.bigodeautopecas.backend.dto.ItemCarrinhoDTO;
import com.bigodeautopecas.backend.dto.ProdutoResumoDTO;
import com.bigodeautopecas.backend.exception.ResourceNotFoundException;
import com.bigodeautopecas.backend.model.Carrinho;
import com.bigodeautopecas.backend.model.ItemCarrinho;
import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.CarrinhoRepository;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CarrinhoService {

    private final CarrinhoRepository repo;
    private final ProdutoRepository produtoRepo;
    private final UsuarioRepository usuarioRepo;

    public CarrinhoService(CarrinhoRepository repo, ProdutoRepository produtoRepo, UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.produtoRepo = produtoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // ── Leitura (retornam DTO) ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CarrinhoDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<CarrinhoDTO> listarPorEmail(String email) {
        return repo.findByUsuarioEmail(email)
                .map(c -> List.of(toDTO(c)))
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public CarrinhoDTO buscarPorIdComoDTO(Long id) {
        return toDTO(buscarPorId(id));
    }

    // ── Escrita (retornam DTO) ────────────────────────────────────────────────

    @Transactional
    public CarrinhoDTO salvarComoDTO(Carrinho c) {
        return toDTO(repo.save(c));
    }

    @Transactional
    public CarrinhoDTO atualizarItens(Long id, List<ItemCarrinho> novosItens, Usuario novoUsuario) {
        Carrinho carrinho = buscarPorId(id);
        carrinho.setItens(novosItens);
        if (novoUsuario != null) carrinho.setUsuario(novoUsuario);
        return toDTO(repo.save(carrinho));
    }

    @Transactional
    public CarrinhoDTO adicionarItem(String email, Long produtoId, Integer quantidade) {
        Produto produto = produtoRepo.findById(produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + produtoId));

        Carrinho carrinho = repo.findByUsuarioEmail(email).orElseGet(() -> {
            Usuario usuario = usuarioRepo.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
            Carrinho novo = new Carrinho();
            novo.setUsuario(usuario);
            return repo.save(novo);
        });

        carrinho.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(produtoId))
                .findFirst()
                .ifPresentOrElse(
                        i -> i.setQuantidade(i.getQuantidade() + quantidade),
                        () -> {
                            ItemCarrinho item = new ItemCarrinho();
                            item.setProduto(produto);
                            item.setQuantidade(quantidade);
                            carrinho.getItens().add(item);
                        }
                );

        return toDTO(repo.save(carrinho));
    }

    @Transactional
    public CarrinhoDTO removerItem(String email, Long itemId) {
        Carrinho carrinho = repo.findByUsuarioEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para: " + email));

        boolean removed = carrinho.getItens().removeIf(i -> i.getId().equals(itemId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado no carrinho");
        }

        return toDTO(repo.save(carrinho));
    }

    // ── Uso interno ──────────────────────────────────────────────────────────

    /** Retorna entidade para uso interno (modificações, verificações). */
    public Carrinho buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado: " + id));
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repo.deleteById(id);
    }

    // ── Mapeamento ───────────────────────────────────────────────────────────

    /** Deve ser chamado dentro de um contexto @Transactional para que o lazy loading funcione. */
    private CarrinhoDTO toDTO(Carrinho c) {
        List<ItemCarrinhoDTO> itens = c.getItens().stream()
                .map(i -> new ItemCarrinhoDTO(
                        i.getId(),
                        i.getQuantidade(),
                        new ProdutoResumoDTO(
                                i.getProduto().getId(),
                                i.getProduto().getNome(),
                                i.getProduto().getPreco(),
                                i.getProduto().getImagemUrl(),
                                i.getProduto().getCategoria(),
                                i.getProduto().getMarca()
                        )
                )).toList();

        double total = itens.stream()
                .mapToDouble(i -> i.produto().preco() * i.quantidade())
                .sum();

        String email = c.getUsuario() != null ? c.getUsuario().getEmail() : null;

        return new CarrinhoDTO(c.getId(), email, itens, total);
    }
}
