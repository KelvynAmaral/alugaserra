package com.alugaserra.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Controller "Fantasma" ---

    @TestConfiguration
    static class TestControllerConfiguration {
        @RestController
        class TestController {
            @GetMapping("/test/illegal-state")
            public ResponseEntity<Void> throwIllegalStateException() {
                throw new IllegalStateException("Violação de regra de negócio de teste.");
            }

            @GetMapping("/test/entity-not-found")
            public ResponseEntity<Void> throwEntityNotFoundException() {
                throw new EntityNotFoundException("Entidade de teste não encontrada.");
            }
        }
    }

    @Test
    @DisplayName("Deve capturar IllegalStateException e retornar status 409 Conflict")
    @WithMockUser // <--Executa este teste como um usuário autenticado.
    void handleIllegalStateException_ShouldReturnConflict() throws Exception {
        mockMvc.perform(get("/test/illegal-state"))
                .andExpect(status().isConflict()) // Verifica o status HTTP 409
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Violação de regra de negócio de teste."));
    }

    @Test
    @DisplayName("Deve capturar EntityNotFoundException e retornar status 404 Not Found")
    @WithMockUser // <-- CORREÇÃO: Executa este teste como um usuário autenticado.
    void handleEntityNotFoundException_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/test/entity-not-found"))
                .andExpect(status().isNotFound()) // Verifica o status HTTP 404
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Entidade de teste não encontrada."));
    }
}

