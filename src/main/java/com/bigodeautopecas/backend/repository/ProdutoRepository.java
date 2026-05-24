package com.bigodeautopecas.backend.repository;

import com.bigodeautopecas.backend.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByCategoria(String categoria, Pageable pageable);
    Page<Produto> findByMarca(String marca, Pageable pageable);
    Page<Produto> findByModeloContainingIgnoreCase(String modelo, Pageable pageable);
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}