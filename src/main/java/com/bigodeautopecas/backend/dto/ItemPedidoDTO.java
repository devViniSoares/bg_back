package com.bigodeautopecas.backend.dto;

public record ItemPedidoDTO(
        Long id,
        Integer quantidade,
        Double precoUnitario,
        ProdutoResumoDTO produto
) {}
