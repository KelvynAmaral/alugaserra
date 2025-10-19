package com.alugaserra.service;

import com.alugaserra.model.User;
import com.alugaserra.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    @DisplayName("Deve retornar UserDetails quando o usuário é encontrado pelo email")
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // --- Cenário (Arrange) ---
        String email = "usuario.existente@email.com";
        User user = new User();
        user.setEmail(email);

        // Dizemos ao nosso mock do repositório para retornar o nosso usuário de teste
        // quando o método findByEmail for chamado com este email.
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // --- Ação (Act) ---
        UserDetails userDetails = authorizationService.loadUserByUsername(email);

        // --- Verificação (Assert) ---
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o usuário não é encontrado")
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // --- Cenário (Arrange) ---
        String email = "email.inexistente@email.com";

        // Dizemos ao nosso mock para retornar um Optional vazio, simulando que não encontrou o usuário.
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // --- Ação e Verificação (Act & Assert) ---
        // Verificamos se a exceção correta é lançada ao chamar o método.
        assertThrows(UsernameNotFoundException.class, () -> {
            authorizationService.loadUserByUsername(email);
        });
    }
}
