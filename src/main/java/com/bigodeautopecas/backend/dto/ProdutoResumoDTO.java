package com.bigodeautopecas.backend.dto;

public record ProdutoResumoDTO(
        Long id,
        String nome,
        Double preco,
        String imagemUrl,
        String categoria,
        String marca
) {}
