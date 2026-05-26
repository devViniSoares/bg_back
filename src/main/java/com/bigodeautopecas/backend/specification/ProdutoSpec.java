package com.bigodeautopecas.backend.specification;

import com.bigodeautopecas.backend.model.Produto;
import org.springframework.data.jpa.domain.Specification;

public class ProdutoSpec {

    private ProdutoSpec() {}

    public static Specification<Produto> comCategoria(String categoria) {
        return (root, query, cb) ->
                (categoria == null || categoria.isBlank()) ? null
                        : cb.equal(root.get("categoria"), categoria);
    }

    public static Specification<Produto> comMarca(String marca) {
        return (root, query, cb) ->
                (marca == null || marca.isBlank()) ? null
                        : cb.equal(root.get("marca"), marca);
    }

    public static Specification<Produto> comModelo(String modelo) {
        return (root, query, cb) ->
                (modelo == null || modelo.isBlank()) ? null
                        : cb.like(cb.lower(root.get("modelo")), "%" + modelo.toLowerCase() + "%");
    }

    public static Specification<Produto> comNome(String nome) {
        return (root, query, cb) ->
                (nome == null || nome.isBlank()) ? null
                        : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }
}