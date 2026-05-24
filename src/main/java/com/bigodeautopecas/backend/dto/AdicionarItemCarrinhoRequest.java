package com.bigodeautopecas.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdicionarItemCarrinhoRequest(
        @NotNull Long produtoId,
        @NotNull @Min(1) Integer quantidade
) {}