package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.dto.EnderecoViaCEPResponse;
import com.bigodeautopecas.backend.exception.NegocioException;
import com.bigodeautopecas.backend.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CepService {

    private static final Logger log = LoggerFactory.getLogger(CepService.class);

    private final RestClient restClient;

    public CepService(@Value("${viacep.base-url:https://viacep.com.br/ws}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public EnderecoViaCEPResponse buscar(String cep) {
        String cepLimpo = sanitizar(cep);

        if (!cepLimpo.matches("\\d{8}")) {
            throw new NegocioException("CEP inválido: deve conter 8 dígitos numéricos");
        }

        EnderecoViaCEPResponse response;
        try {
            response = restClient.get()
                    .uri("/{cep}/json/", cepLimpo)
                    .retrieve()
                    .body(EnderecoViaCEPResponse.class);
        } catch (RestClientException e) {
            log.error("Erro ao consultar ViaCEP para o CEP {}: {}", cepLimpo, e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Serviço de CEP temporariamente indisponível. Tente novamente em instantes.");
        }

        if (response == null || Boolean.TRUE.equals(response.erro())) {
            throw new ResourceNotFoundException("CEP não encontrado: " + cep);
        }

        return response;
    }

    private String sanitizar(String cep) {
        if (cep == null) return "";
        return cep.replaceAll("[^0-9]", "");
    }
}