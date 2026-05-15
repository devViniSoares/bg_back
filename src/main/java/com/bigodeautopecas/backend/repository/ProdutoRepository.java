package com.bigodeautopecas.backend.repository;

import com.bigodeautopecas.backend.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByCategoria(String categoria);
    List<Produto> findByMarca(String marca);
    List<Produto> findByModeloContainingIgnoreCase(String modelo);
    List<Produto> findByNomeContainingIgnoreCase(String nome);
}
