package com.bigodeautopecas.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void cadastro_deveRetornar201AoCriarUsuario() throws Exception {
        Map<String, String> body = Map.of(
                "nome", "Teste Usuario",
                "email", "novouser@bigode.com",
                "senha", "senha123"
        );

        mockMvc.perform(post("/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void cadastro_deveRetornar409SeEmailJaExiste() throws Exception {
        Map<String, String> body = Map.of(
                "nome", "Duplicado",
                "email", "duplicado@bigode.com",
                "senha", "senha123"
        );

        mockMvc.perform(post("/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_deveRetornarTokenJwtComCredenciaisValidas() throws Exception {
        Map<String, String> cadastro = Map.of(
                "nome", "Login Teste",
                "email", "logintest@bigode.com",
                "senha", "senha123"
        );
        mockMvc.perform(post("/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cadastro)));

        Map<String, String> login = Map.of(
                "email", "logintest@bigode.com",
                "senha", "senha123"
        );
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("CLIENTE"));
    }

    @Test
    void login_deveRetornar401ComCredenciaisInvalidas() throws Exception {
        Map<String, String> body = Map.of(
                "email", "inexistente@bigode.com",
                "senha", "senhaErrada"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastro_deveRetornar400SeSenhaForCurta() throws Exception {
        Map<String, String> body = Map.of(
                "nome", "Teste",
                "email", "curto@bigode.com",
                "senha", "123"
        );

        mockMvc.perform(post("/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}