package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.exception.ResourceNotFoundException;
import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import com.bigodeautopecas.backend.specification.ProdutoSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repo;

    public ProdutoService(ProdutoRepository repo) {
        this.repo = repo;
    }

    public Page<Produto> listarComFiltros(String categoria, String marca, String modelo, String nome, Pageable pageable) {
        Specification<Produto> spec = Specification
                .where(ProdutoSpec.comCategoria(categoria))
                .and(ProdutoSpec.comMarca(marca))
                .and(ProdutoSpec.comModelo(modelo))
                .and(ProdutoSpec.comNome(nome));
        return repo.findAll(spec, pageable);
    }

    public List<String> listarCategorias() {
        return repo.findCategoriasDistintas();
    }

    public List<String> listarMarcas() {
        return repo.findMarcasDistintas();
    }

    public List<String> listarModelos() {
        return repo.findModelosDistintos();
    }

    public Produto buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    public Produto salvar(Produto produto) {
        return repo.save(produto);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repo.deleteById(id);
    }
}