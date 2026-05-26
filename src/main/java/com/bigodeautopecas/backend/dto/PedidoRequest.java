package com.bigodeautopecas.backend.dto;

import com.bigodeautopecas.backend.model.Endereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PedidoRequest(
        @NotEmpty(message = "O pedido deve ter ao menos um item")
        @Valid List<ItemPedidoRequest> itens,

        @Valid Endereco enderecoEntrega
) {}