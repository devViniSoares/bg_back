package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.Carrinho;
import com.bigodeautopecas.backend.repository.CarrinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository repo;

    public List<Carrinho> listar() {
        return repo.findAll();
    }

    public Carrinho salvar(Carrinho c) {
        return repo.save(c);
    }

    public Carrinho buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));
    }

    public void deletar(Long id) {
        repo.deleteById(id);
    }
}
