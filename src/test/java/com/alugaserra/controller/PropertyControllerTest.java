package com.alugaserra.controller;

import com.alugaserra.dto.PropertyCreateDto;
import com.alugaserra.enums.PropertyType;
import com.alugaserra.enums.UserRole;
import com.alugaserra.model.Plan;
import com.alugaserra.model.Subscription;
import com.alugaserra.model.User;
import com.alugaserra.repository.PlanRepository;
import com.alugaserra.repository.SubscriptionRepository;
import com.alugaserra.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // --- VARIÁVEIS PARA OS NOSSOS USUÁRIOS DE TESTE ---
    private User locador;
    private User inquilino;

    @BeforeEach
    void setupDatabase() {
        // --- PREPARAÇÃO DOS DADOS ---
        // Cria e salva um usuário LOCADOR real no banco de dados de teste
        locador = new User();
        locador.setEmail("locador.teste@email.com");
        locador.setPasswordHash(passwordEncoder.encode("password"));
        locador.setRole(UserRole.LOCADOR);
        userRepository.save(locador);

        // Cria e salva um usuário INQUILINO real
        inquilino = new User();
        inquilino.setEmail("inquilino.teste@email.com");
        inquilino.setPasswordHash(passwordEncoder.encode("password"));
        inquilino.setRole(UserRole.INQUILINO);
        userRepository.save(inquilino);

        // Garante que o LOCADOR tenha uma assinatura ativa
        Plan plan = planRepository.findByName("Bronze").orElseGet(() -> {
            Plan newPlan = new Plan();
            newPlan.setName("Bronze");
            newPlan.setMaxProperties(1);
            return planRepository.save(newPlan);
        });

        Subscription subscription = new Subscription();
        subscription.setUser(locador);
        subscription.setPlan(plan);
        subscription.setStatus("ACTIVE");
        subscription.setStartDate(LocalDate.now());
        subscriptionRepository.save(subscription);
    }

    @Test
    @DisplayName("Deve permitir que um LOCADOR crie um imóvel com sucesso")
    void createProperty_ShouldSucceed_WhenUserIsLocador() throws Exception {
        PropertyCreateDto propertyDto = new PropertyCreateDto();
        propertyDto.setTitle("Casa de Teste");
        propertyDto.setDescription("Descrição da casa de teste.");
        propertyDto.setType(PropertyType.CASA);
        propertyDto.setRentValue(1500.00);
        propertyDto.setRooms(3);
        propertyDto.setBathrooms(2);
        propertyDto.setHasGarage(true);
        propertyDto.setIsFurnished(false);
        propertyDto.setPhotoUrls(Collections.emptyList());
        propertyDto.setApproximateLocation("Centro");

        mockMvc.perform(post("/api/properties")
                        .with(user(locador)) // <-- CORREÇÃO: Anexa o nosso usuário REAL à requisição
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Casa de Teste"));
    }

    @Test
    @DisplayName("Deve proibir que um INQUILINO crie um imóvel")
    void createProperty_ShouldFail_WhenUserIsInquilino() throws Exception {
        PropertyCreateDto propertyDto = new PropertyCreateDto();

        mockMvc.perform(post("/api/properties")
                        .with(user(inquilino)) // <-- CORREÇÃO: Anexa o nosso usuário INQUILINO à requisição
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve proibir que um usuário não autenticado crie um imóvel")
    void createProperty_ShouldFail_WhenUserIsUnauthenticated() throws Exception {
        PropertyCreateDto propertyDto = new PropertyCreateDto();

        mockMvc.perform(post("/api/properties")
                        // Nenhuma autenticação é anexada aqui
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyDto)))
                .andExpect(status().isForbidden());
    }
}

