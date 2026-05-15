package com.bigodeautopecas.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequest(
        @NotBlank(message = "Senha atual obrigatória") String senhaAtual,
        @NotBlank @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres") String novaSenha
) {}
