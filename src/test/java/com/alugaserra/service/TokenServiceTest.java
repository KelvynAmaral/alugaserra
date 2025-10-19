package com.alugaserra.service;

import com.alugaserra.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

// Este é um teste de unidade puro, não precisa de anotações do Spring.
class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        // Como não estamos num ambiente Spring, injetamos os valores do application-test.properties
        // manualmente usando uma ferramenta de teste chamada ReflectionTestUtils.
        ReflectionTestUtils.setField(tokenService, "secret", "MinhaChaveSecretaSuperLongaParaProtegerMeuTokenJWTDoAlugaSerra");
        ReflectionTestUtils.setField(tokenService, "expiration", 3600000L);
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido e extrair o email corretamente")
    void generateToken_ShouldCreateValidTokenAndExtractSubject() {
        // --- Cenário (Arrange) ---
        User user = new User();
        user.setEmail("usuario.teste@email.com");

        // --- Ação (Act) ---
        String token = tokenService.generateToken(user);
        String subject = tokenService.validateToken(token);

        // --- Verificação (Assert) ---
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(subject).isEqualTo("usuario.teste@email.com");
    }

    @Test
    @DisplayName("Deve retornar uma string vazia ao validar um token inválido")
    void validateToken_ShouldReturnEmptyString_WhenTokenIsInvalid() {
        // --- Cenário (Arrange) ---
        String invalidToken = "token.invalido.jwt";

        // --- Ação (Act) ---
        String subject = tokenService.validateToken(invalidToken);

        // --- Verificação (Assert) ---
        assertThat(subject).isEmpty();
    }
}
