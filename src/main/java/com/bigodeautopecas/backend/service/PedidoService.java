package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.model.Pedido;
import com.bigodeautopecas.backend.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repo;

    public PedidoService(PedidoRepository repo) {
        this.repo = repo;
    }

    public List<Pedido> listar() { return repo.findAll(); }

    public List<Pedido> listarPorEmail(String email) {
        return repo.findByUsuarioEmail(email);
    }

    public Pedido salvar(Pedido p) { return repo.save(p); }

    public Pedido buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
    }

    public void deletar(Long id) { repo.deleteById(id); }
}
