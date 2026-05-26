package com.bigodeautopecas.backend.repository;

import com.bigodeautopecas.backend.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    @Query("SELECT DISTINCT p.categoria FROM Produto p WHERE p.categoria IS NOT NULL AND p.categoria <> '' ORDER BY p.categoria")
    List<String> findCategoriasDistintas();

    @Query("SELECT DISTINCT p.marca FROM Produto p WHERE p.marca IS NOT NULL AND p.marca <> '' ORDER BY p.marca")
    List<String> findMarcasDistintas();

    @Query("SELECT DISTINCT p.modelo FROM Produto p WHERE p.modelo IS NOT NULL AND p.modelo <> '' ORDER BY p.modelo")
    List<String> findModelosDistintos();
}
