package com.bigodeautopecas.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AtualizarPedidoRequest(
        @NotBlank(message = "Status é obrigatório")
        @Pattern(
            regexp = "AGUARDANDO|CONFIRMADO|ENTREGUE|CANCELADO",
            message = "Status inválido. Valores aceitos: AGUARDANDO, CONFIRMADO, ENTREGUE, CANCELADO"
        )
        String status
) {}