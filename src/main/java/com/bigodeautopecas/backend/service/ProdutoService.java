package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository repo;

    public ProdutoService(ProdutoRepository repo) {
        this.repo = repo;
    }

    public Page<Produto> listarComFiltros(String categoria, String marca, String modelo, String nome, Pageable pageable) {
        if (categoria != null && !categoria.isBlank()) return repo.findByCategoria(categoria, pageable);
        if (marca     != null && !marca.isBlank())     return repo.findByMarca(marca, pageable);
        if (modelo    != null && !modelo.isBlank())    return repo.findByModeloContainingIgnoreCase(modelo, pageable);
        if (nome      != null && !nome.isBlank())      return repo.findByNomeContainingIgnoreCase(nome, pageable);
        return repo.findAll(pageable);
    }

    public Produto buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }

    public Produto salvar(Produto produto) {
        return repo.save(produto);
    }

    public void deletar(Long id) {
        repo.deleteById(id);
    }
}