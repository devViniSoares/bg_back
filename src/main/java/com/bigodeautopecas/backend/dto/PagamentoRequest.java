package com.bigodeautopecas.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PagamentoRequest(
        @NotNull Long pedidoId,
        @NotNull @Positive Double valor,
        @NotBlank String metodoPagamento
) {}