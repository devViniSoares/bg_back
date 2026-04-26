package com.bigodeautopecas.backend.repository;

import com.bigodeautopecas.backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {}
