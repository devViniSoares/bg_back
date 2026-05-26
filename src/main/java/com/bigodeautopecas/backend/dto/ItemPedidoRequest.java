package com.bigodeautopecas.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoRequest(
        @NotNull(message = "ID do produto é obrigatório") Long produtoId,
        @NotNull @Min(value = 1, message = "Quantidade mínima é 1") Integer quantidade
) {}