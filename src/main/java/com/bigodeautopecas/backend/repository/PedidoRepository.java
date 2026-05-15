package com.bigodeautopecas.backend.repository;

import com.bigodeautopecas.backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioEmail(String email);
}
