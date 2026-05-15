package com.bigodeautopecas.backend.dto;

public record LoginResponse(String token, String tipo, Long id, String nome) {}
