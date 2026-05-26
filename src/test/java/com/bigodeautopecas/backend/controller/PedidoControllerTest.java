package com.bigodeautopecas.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String clienteToken;
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        String clienteEmail = "pedido-cli-" + UUID.randomUUID() + "@bigode.com";

        mockMvc.perform(post("/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "nome", "Cliente Pedido",
                        "email", clienteEmail,
                        "senha", "senha123"
                ))));

        MvcResult clienteLogin = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", clienteEmail,
                        "senha", "senha123"
                ))))
                .andReturn();
        Map<?, ?> clienteBody = objectMapper.readValue(clienteLogin.getResponse().getContentAsString(), Map.class);
        clienteToken = (String) clienteBody.get("token");

        MvcResult adminLogin = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "admin@bigodeautopecas.com",
                        "senha", "admin123"
                ))))
                .andReturn();
        Map<?, ?> adminBody = objectMapper.readValue(adminLogin.getResponse().getContentAsString(), Map.class);
        adminToken = (String) adminBody.get("token");
    }

    @Test
    void listar_clienteVeListaVaziaInicialmente() throws Exception {
        mockMvc.perform(get("/pedidos")
                .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void listar_adminVeTodosOsPedidos() throws Exception {
        mockMvc.perform(get("/pedidos")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void listar_deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deletar_deveRetornar403ParaCliente() throws Exception {
        mockMvc.perform(delete("/pedidos/1")
                .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorId_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(get("/pedidos/99999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void salvar_deveRetornar400ComBodyVazio() throws Exception {
        mockMvc.perform(post("/pedidos")
                .header("Authorization", "Bearer " + clienteToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}