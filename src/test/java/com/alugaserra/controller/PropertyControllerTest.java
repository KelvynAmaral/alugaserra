package com.alugaserra.controller;

import com.alugaserra.config.DataInitializer;
import com.alugaserra.dto.PropertyCreateDto;
import com.alugaserra.dto.PropertyUpdateDto;
import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.enums.PropertyType;
import com.alugaserra.enums.UserRole;
import com.alugaserra.model.Plan;
import com.alugaserra.model.Property;
import com.alugaserra.model.Subscription;
import com.alugaserra.model.User;
import com.alugaserra.repository.PlanRepository;
import com.alugaserra.repository.PropertyRepository;
import com.alugaserra.repository.SubscriptionRepository;
import com.alugaserra.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // <-- 1. IMPORTAR
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class PropertyControllerTest {

    // 2. CORREÇÃO: "Mocamos" o DataInitializer para impedi-lo de ser executado durante os testes.
    @MockBean
    private DataInitializer dataInitializer;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private PropertyRepository propertyRepository;
    @Autowired private PlanRepository planRepository;
    @Autowired private SubscriptionRepository subscriptionRepository;

    private User locadorOwner;
    private User locadorNotOwner;
    private User inquilino;
    private Property propertyToTest;

    @BeforeEach
    void setupDatabase() {
        // Agora, este método é a única fonte de dados para os nossos testes,
        // garantindo um ambiente limpo e previsível.
        propertyRepository.deleteAll();
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();
        planRepository.deleteAll();

        locadorOwner = new User();
        locadorOwner.setName("Locador Dono");
        locadorOwner.setEmail("locador.owner@email.com");
        locadorOwner.setPasswordHash(passwordEncoder.encode("password"));
        locadorOwner.setRole(UserRole.LOCADOR);
        userRepository.save(locadorOwner);

        locadorNotOwner = new User();
        locadorNotOwner.setEmail("outro.locador@email.com");
        locadorNotOwner.setPasswordHash(passwordEncoder.encode("password"));
        locadorNotOwner.setRole(UserRole.LOCADOR);
        userRepository.save(locadorNotOwner);

        inquilino = new User();
        inquilino.setEmail("inquilino.teste@email.com");
        inquilino.setPasswordHash(passwordEncoder.encode("password"));
        inquilino.setRole(UserRole.INQUILINO);
        userRepository.save(inquilino);

        Plan plan = new Plan();
        plan.setName("Bronze");
        plan.setMaxProperties(2);
        planRepository.save(plan);

        Subscription subscription = new Subscription();
        subscription.setUser(locadorOwner);
        subscription.setPlan(plan);
        subscription.setStatus("ACTIVE");
        subscription.setStartDate(LocalDate.now());
        subscriptionRepository.save(subscription);

        Property casaGrande = new Property();
        casaGrande.setOwner(locadorOwner);
        casaGrande.setTitle("Casa Grande com Quintal");
        casaGrande.setType(PropertyType.CASA);
        casaGrande.setRentValue(2500.00);
        casaGrande.setRooms(4);
        casaGrande.setStatus(PropertyStatus.ACTIVE);

        Property aptoCentro = new Property();
        aptoCentro.setOwner(locadorOwner);
        aptoCentro.setTitle("Apartamento no Centro");
        aptoCentro.setType(PropertyType.APARTAMENTO);
        aptoCentro.setRentValue(1200.00);
        aptoCentro.setRooms(2);
        aptoCentro.setStatus(PropertyStatus.ACTIVE);

        Property kitnetEstudante = new Property();
        kitnetEstudante.setOwner(locadorOwner);
        kitnetEstudante.setTitle("Kitnet ideal para estudantes");
        kitnetEstudante.setType(PropertyType.KITNET);
        kitnetEstudante.setRentValue(800.00);
        kitnetEstudante.setRooms(1);
        kitnetEstudante.setStatus(PropertyStatus.ACTIVE);

        propertyRepository.saveAll(List.of(casaGrande, aptoCentro, kitnetEstudante));

        propertyToTest = casaGrande;
    }

    @Test
    @DisplayName("Deve retornar todos os 3 imóveis ativos quando nenhum filtro é aplicado")
    void searchProperties_ShouldReturnAllActiveProperties_WhenNoFilterIsApplied() throws Exception {
        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))); // Agora a asserção estará correta
    }

    // ... O RESTANTE DOS SEUS TESTES CONTINUA AQUI ...

    @Test
    @DisplayName("Deve permitir que o DONO do imóvel o atualize com sucesso")
    void updateProperty_ShouldSucceed_WhenUserIsOwner() throws Exception {
        PropertyUpdateDto updateDto = new PropertyUpdateDto(
                "Título Atualizado", "Descrição válida", PropertyType.CASA, PropertyStatus.PAUSED,
                2600.0, 4, 3, true, true, Collections.emptyList(), "", "Nova Localização Válida"
        );

        mockMvc.perform(put("/api/properties/" + propertyToTest.getId())
                        .with(user(locadorOwner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título Atualizado"));
    }

    @Test
    @DisplayName("Deve PROIBIR que um LOCADOR que não é o dono atualize o imóvel")
    void updateProperty_ShouldFail_WhenUserIsNotOwner() throws Exception {
        PropertyUpdateDto updateDto = new PropertyUpdateDto(
                "Título Malicioso", "Descrição Válida", PropertyType.CASA, PropertyStatus.ACTIVE,
                1.0, 1, 1, false, false, Collections.emptyList(), "", "Local Válido"
        );

        mockMvc.perform(put("/api/properties/" + propertyToTest.getId())
                        .with(user(locadorNotOwner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve PROIBIR que um INQUILINO atualize o imóvel")
    void updateProperty_ShouldFail_WhenUserIsInquilino() throws Exception {
        PropertyUpdateDto updateDto = new PropertyUpdateDto(
                "Título Malicioso", "Descrição Válida", PropertyType.CASA, PropertyStatus.ACTIVE,
                1.0, 1, 1, false, false, Collections.emptyList(), "", "Local Válido"
        );

        mockMvc.perform(put("/api/properties/" + propertyToTest.getId())
                        .with(user(inquilino))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve permitir que o DONO do imóvel o delete com sucesso")
    void deleteProperty_ShouldSucceed_WhenUserIsOwner() throws Exception {
        mockMvc.perform(delete("/api/properties/" + propertyToTest.getId())
                        .with(user(locadorOwner)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve PROIBIR que um LOCADOR que não é o dono delete o imóvel")
    void deleteProperty_ShouldFail_WhenUserIsNotOwner() throws Exception {
        mockMvc.perform(delete("/api/properties/" + propertyToTest.getId())
                        .with(user(locadorNotOwner)))
                .andExpect(status().isForbidden());
    }
}

