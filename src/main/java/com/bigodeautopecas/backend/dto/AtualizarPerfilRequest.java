package com.bigodeautopecas.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AtualizarPerfilRequest(
        @NotBlank(message = "Nome obrigatório") String nome,
        @NotBlank @Email(message = "E-mail inválido") String email
) {}
