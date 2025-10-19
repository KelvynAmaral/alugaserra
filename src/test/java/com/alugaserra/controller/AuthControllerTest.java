package com.alugaserra.controller;

import com.alugaserra.dto.RegisterRequestDto;
import com.alugaserra.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional// Garante que a base de dados seja limpa após cada teste.
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Utilitário para converter objetos Java para JSON

    private RegisterRequestDto locadorDto;

    @BeforeEach
    void setUp() {
        // Prepara um DTO de utilizador para ser usado nos testes
        locadorDto = new RegisterRequestDto(
                "Teste Locador",
                "locador.teste@email.com",
                "senha123",
                "35526718620", //
                "31988776655",
                UserRole.LOCADOR
        );
    }

    @Test
    @DisplayName("Deve registar um novo utilizador com sucesso e retornar o status 201")
    void register_ShouldReturnCreated_WhenUserIsNew() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locadorDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve falhar ao registar um utilizador com email já existente e retornar o status 400")
    void register_ShouldReturnBadRequest_WhenEmailAlreadyExists() throws Exception {
        // Primeiro, regista o utilizador (este teste agora vai passar)
        register_ShouldReturnCreated_WhenUserIsNew();

        // Depois, tenta registar novamente com o mesmo e-mail
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locadorDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve autenticar um utilizador válido e retornar um token JWT")
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        // Primeiro, regista o utilizador para garantir que ele exista
        register_ShouldReturnCreated_WhenUserIsNew();

        // Prepara o corpo da requisição de login
        String loginRequestJson = """
                {
                    "email": "locador.teste@email.com",
                    "password": "senha123"
                }
                """;

        // Tenta fazer o login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}

