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

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PerfilControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token;
    private String email;

    @BeforeEach
    void setUp() throws Exception {
        email = "perfil-" + UUID.randomUUID() + "@bigode.com";

        mockMvc.perform(post("/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "nome", "Teste Perfil",
                        "email", email,
                        "senha", "senha123"
                ))));

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "senha", "senha123"
                ))))
                .andReturn();

        Map<?, ?> loginBody = objectMapper.readValue(loginResult.getResponse().getContentAsString(), Map.class);
        token = (String) loginBody.get("token");
    }

    @Test
    void meuPerfil_deveRetornarDadosDoUsuarioAutenticado() throws Exception {
        mockMvc.perform(get("/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.tipo").value("CLIENTE"));
    }

    @Test
    void atualizarPerfil_deveAlterarNomeERetornar200() throws Exception {
        String novoEmail = "perfil-upd-" + UUID.randomUUID() + "@bigode.com";

        mockMvc.perform(put("/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "nome", "Nome Atualizado",
                        "email", novoEmail
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Atualizado"));
    }

    @Test
    void alterarSenha_deveRetornar200ComSenhaCorreta() throws Exception {
        mockMvc.perform(put("/me/senha")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "senhaAtual", "senha123",
                        "novaSenha", "novaSenha456"
                ))))
                .andExpect(status().isOk());
    }

    @Test
    void alterarSenha_deveRetornar400ComSenhaAtualIncorreta() throws Exception {
        mockMvc.perform(put("/me/senha")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "senhaAtual", "senhaErrada",
                        "novaSenha", "novaSenha456"
                ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void meuPerfil_deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/me"))
                .andExpect(status().isUnauthorized());
    }
}