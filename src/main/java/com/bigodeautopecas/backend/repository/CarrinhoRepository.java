package com.bigodeautopecas.backend.repository;

import com.bigodeautopecas.backend.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {}