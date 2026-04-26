package com.bigodeautopecas.backend.repository;

import com.bigodeautopecas.backend.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {}
