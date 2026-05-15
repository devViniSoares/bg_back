package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.service.ProdutoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@CrossOrigin
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Produto> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String nome) {
        return service.listarComFiltros(categoria, marca, modelo, nome);
    }

    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Produto salvar(@RequestBody Produto produto) {
        return service.salvar(produto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Produto atualizar(@PathVariable Long id, @RequestBody Produto novoProduto) {
        Produto produto = service.buscarPorId(id);
        produto.setNome(novoProduto.getNome());
        produto.setDescricao(novoProduto.getDescricao());
        produto.setPreco(novoProduto.getPreco());
        produto.setEstoque(novoProduto.getEstoque());
        produto.setCategoria(novoProduto.getCategoria());
        produto.setMarca(novoProduto.getMarca());
        produto.setModelo(novoProduto.getModelo());
        produto.setImagemUrl(novoProduto.getImagemUrl());
        return service.salvar(produto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
