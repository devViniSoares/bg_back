package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.dto.PagamentoRequest;
import com.bigodeautopecas.backend.dto.PagamentoResponse;

public interface PagamentoService {
    PagamentoResponse processar(PagamentoRequest request);
}