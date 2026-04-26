package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repo;

    public List<Produto> listar() {
        return repo.findAll();
    }

    public Produto salvar(Produto p) {
        return repo.save(p);
    }

    public Produto buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public void deletar(Long id) {
        repo.deleteById(id);
    }
}
