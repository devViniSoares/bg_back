package com.bigodeautopecas.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email(message = "E-mail inválido") String email,
        @NotBlank(message = "Senha obrigatória") String senha
) {}
