package com.alugaserra.controller;

import com.alugaserra.enums.UserRole;
import com.alugaserra.model.User;
import com.alugaserra.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- USUÁRIOS DE TESTE ---
    private User adminUser;
    private User locadorUser;

    @BeforeEach
    void setUp() {
        // Cria e salva um usuário ADMIN no banco de dados de teste
        adminUser = new User();
        adminUser.setEmail("admin.test@email.com");
        adminUser.setPasswordHash(passwordEncoder.encode("password"));
        adminUser.setRole(UserRole.ADMIN);
        userRepository.save(adminUser);

        // Cria e salva um usuário LOCADOR que será o alvo das nossas operações
        locadorUser = new User();
        locadorUser.setEmail("locador.target@email.com");
        locadorUser.setPasswordHash(passwordEncoder.encode("password"));
        locadorUser.setRole(UserRole.LOCADOR);
        userRepository.save(locadorUser);
    }

    @Test
    @DisplayName("Deve permitir que um ADMIN liste todos os usuários")
    void getAllUsers_ShouldSucceed_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .with(user(adminUser))) // Simula a requisição como ADMIN
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve PROIBIR que um LOCADOR liste todos os usuários")
    void getAllUsers_ShouldFail_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .with(user(locadorUser))) // Simula a requisição como LOCADOR
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve permitir que um ADMIN altere o papel de um usuário")
    void updateUserRole_ShouldSucceed_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(put("/api/admin/users/" + locadorUser.getId() + "/role")
                        .with(user(adminUser)) // Simula a requisição como ADMIN
                        .param("newRole", "INQUILINO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("INQUILINO"));
    }

    @Test
    @DisplayName("Deve PROIBIR que um LOCADOR altere o papel de um usuário")
    void updateUserRole_ShouldFail_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(put("/api/admin/users/" + locadorUser.getId() + "/role")
                        .with(user(locadorUser)) // Simula a requisição como LOCADOR
                        .param("newRole", "ADMIN"))
                .andExpect(status().isForbidden());
    }
}
