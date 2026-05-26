package com.bigodeautopecas.backend.dto;

public record PagamentoResponse(
        String status,
        String codigoTransacao,
        String mensagem
) {}