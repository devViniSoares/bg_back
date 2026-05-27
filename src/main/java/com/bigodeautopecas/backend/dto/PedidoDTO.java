package com.bigodeautopecas.backend.dto;

import com.bigodeautopecas.backend.model.Endereco;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoDTO(
        Long id,
        Double total,
        String status,
        UsuarioResumoDTO usuario,
        Endereco enderecoEntrega,
        List<ItemPedidoDTO> itens,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}
