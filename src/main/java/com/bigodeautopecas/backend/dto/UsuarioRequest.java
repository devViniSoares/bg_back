package com.bigodeautopecas.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UsuarioRequest(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank @Email(message = "E-mail inválido") String email,
        String senha,
        @NotBlank @Pattern(regexp = "ADMIN|CLIENTE", message = "Tipo deve ser ADMIN ou CLIENTE") String tipo
) {}