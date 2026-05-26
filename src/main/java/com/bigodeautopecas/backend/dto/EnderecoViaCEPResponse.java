package com.bigodeautopecas.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EnderecoViaCEPResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,

        @JsonProperty("localidade")
        String cidade,

        @JsonProperty("uf")
        String estado,

        String ibge,
        String ddd,

        Boolean erro
) {}