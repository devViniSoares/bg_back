package com.bigodeautopecas.backend.dto;

public record ItemCarrinhoDTO(
        Long id,
        Integer quantidade,
        ProdutoResumoDTO produto
) {}
