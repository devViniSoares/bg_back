package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.Carrinho;
import com.bigodeautopecas.backend.repository.CarrinhoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrinhoService {

    private final CarrinhoRepository repo;

    public CarrinhoService(CarrinhoRepository repo) {
        this.repo = repo;
    }

    public List<Carrinho> listar() { return repo.findAll(); }

    public List<Carrinho> listarPorEmail(String email) {
        return repo.findByUsuarioEmail(email).map(List::of).orElse(List.of());
    }

    public Carrinho salvar(Carrinho c) { return repo.save(c); }

    public Carrinho buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado: " + id));
    }

    public void deletar(Long id) { repo.deleteById(id); }
}
