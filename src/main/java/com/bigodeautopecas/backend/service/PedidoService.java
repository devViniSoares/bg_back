package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.Pedido;
import com.bigodeautopecas.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repo;

    public List<Pedido> listar() {
        return repo.findAll();
    }

    public Pedido salvar(Pedido p) {
        return repo.save(p);
    }

    public Pedido buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    public void deletar(Long id) {
        repo.deleteById(id);
    }
}
