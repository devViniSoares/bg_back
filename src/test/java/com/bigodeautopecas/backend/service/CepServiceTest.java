package com.bigodeautopecas.backend.service;

import com.bigodeautopecas.backend.exception.NegocioException;
import com.bigodeautopecas.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CepServiceTest {

    private final CepService service = new CepService("https://viacep.com.br/ws");

    @Test
    void buscar_deveLancarExcecaoParaCepComLetras() {
        assertThrows(NegocioException.class, () -> service.buscar("ABCD1234"));
    }

    @Test
    void buscar_deveLancarExcecaoParaCepCurto() {
        assertThrows(NegocioException.class, () -> service.buscar("1234567"));
    }

    @Test
    void buscar_deveLancarExcecaoParaCepLongo() {
        assertThrows(NegocioException.class, () -> service.buscar("123456789"));
    }

    @Test
    void buscar_deveSanitizarHifenAntesDeValidar() {
        // CEP com hífen deve passar na validação (8 dígitos após sanitização)
        // não lança NegocioException — pode lançar ResourceNotFoundException ou conectar à API
        // aqui só validamos que não é NegocioException
        assertThrows(RuntimeException.class, () -> service.buscar("00000-000"));
        // não deve ser NegocioException de formato
        try {
            service.buscar("00000-000");
        } catch (NegocioException e) {
            fail("Não deveria lançar NegocioException para CEP com hífen e 8 dígitos");
        } catch (Exception e) {
            // Pode ser ResourceNotFoundException (CEP inválido) ou ServiceUnavailable — ambos são esperados
        }
    }
}