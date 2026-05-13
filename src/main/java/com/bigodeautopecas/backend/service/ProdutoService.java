package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repo;

    public ProdutoService(ProdutoRepository repo) {
        this.repo = repo;
    }

    public List<Produto> listar() {
        return repo.findAll();
    }

    public List<Produto> listarComFiltros(String categoria, String marca, String modelo, String nome) {
        if (categoria != null && !categoria.isBlank()) return repo.findByCategoria(categoria);
        if (marca     != null && !marca.isBlank())     return repo.findByMarca(marca);
        if (modelo    != null && !modelo.isBlank())    return repo.findByModeloContainingIgnoreCase(modelo);
        if (nome      != null && !nome.isBlank())      return repo.findByNomeContainingIgnoreCase(nome);
        return repo.findAll();
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
