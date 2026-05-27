package com.bigodeautopecas.backend.dto;

import java.util.List;

public record CarrinhoDTO(
        Long id,
        String usuarioEmail,
        List<ItemCarrinhoDTO> itens,
        Double total
) {}
