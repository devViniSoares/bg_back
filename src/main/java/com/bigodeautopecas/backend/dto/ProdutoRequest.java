package com.bigodeautopecas.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProdutoRequest(
        @NotBlank(message = "Nome é obrigatório") String nome,
        String descricao,
        @NotNull(message = "Preço é obrigatório") @Positive(message = "Preço deve ser maior que zero") Double preco,
        @NotNull(message = "Estoque é obrigatório") @PositiveOrZero(message = "Estoque não pode ser negativo") Integer estoque,
        String categoria,
        String marca,
        String modelo,
        String imagemUrl
) {}