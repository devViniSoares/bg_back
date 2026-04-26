package com.bigodeautopecas.backend.controller;

import com.bigodeautopecas.backend.model.Carrinho;
import com.bigodeautopecas.backend.service.CarrinhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrinho")
@CrossOrigin
public class CarrinhoController {

    @Autowired
    private CarrinhoService service;

    @GetMapping
    public List<Carrinho> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Carrinho buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Carrinho salvar(@RequestBody Carrinho carrinho) {
        return service.salvar(carrinho);
    }

    @PutMapping("/{id}")
    public Carrinho atualizar(@PathVariable Long id, @RequestBody Carrinho novoCarrinho) {

        Carrinho carrinho = service.buscarPorId(id);

        carrinho.setUsuario(novoCarrinho.getUsuario());
        carrinho.setItens(novoCarrinho.getItens());

        return service.salvar(carrinho);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}