package com.bigodeautopecas.backend.service;

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

    public List<Carrinho> listar() {
        return repo.findAll();
    }

    public List<Carrinho> listarPorEmail(String email) {
        return repo.findByUsuarioEmail(email).map(List::of).orElse(List.of());
    }

    public Carrinho salvar(Carrinho c) {
        return repo.save(c);
    }

    public Carrinho buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado: " + id));
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repo.deleteById(id);
    }

    @Transactional
    public Carrinho adicionarItem(String email, Long produtoId, Integer quantidade) {
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

        return repo.save(carrinho);
    }

    @Transactional
    public Carrinho removerItem(String email, Long itemId) {
        Carrinho carrinho = repo.findByUsuarioEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para: " + email));

        boolean removed = carrinho.getItens().removeIf(i -> i.getId().equals(itemId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado no carrinho");
        }

        return repo.save(carrinho);
    }
}