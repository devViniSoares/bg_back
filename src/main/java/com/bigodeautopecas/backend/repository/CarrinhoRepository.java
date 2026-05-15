package com.bigodeautopecas.backend.repository;

import com.bigodeautopecas.backend.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByUsuarioEmail(String email);
}
