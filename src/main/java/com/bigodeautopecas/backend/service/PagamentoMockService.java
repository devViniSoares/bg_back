package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.dto.PagamentoRequest;
import com.bigodeautopecas.backend.dto.PagamentoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementação stub do gateway de pagamento.
 * Substituir por integração real (Mercado Pago, Stripe, PagSeguro) em produção.
 */
@Service
@Primary
public class PagamentoMockService implements PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoMockService.class);

    @Override
    public PagamentoResponse processar(PagamentoRequest request) {
        log.info("Processando pagamento mock — pedido #{}, método: {}, valor: R$ {}",
                request.pedidoId(), request.metodoPagamento(), request.valor());

        String codigo = UUID.randomUUID().toString().substring(0, 16).toUpperCase();
        return new PagamentoResponse("APROVADO", codigo, "Pagamento aprovado com sucesso");
    }
}