package com.alugaserra.controller;

import com.alugaserra.config.DataInitializer;
import com.alugaserra.dto.PropertyCreateDto;
import com.alugaserra.dto.PropertyUpdateDto;
import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.enums.PropertyType;
import com.alugaserra.enums.UserRole;
import com.alugaserra.model.Property;
import com.alugaserra.model.User;
import com.alugaserra.repository.PropertyRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private DataInitializer dataInitializer;
    @Autowired private PropertyRepository propertyRepository;

    private User locadorOwner;
    private User locadorNotOwner;
    private User inquilino;
    private Property propertyToTest;

    @BeforeEach
    void setupDatabase() {
        try {
            dataInitializer.run(new String[]{});
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar dados de teste", e);
        }

        locadorOwner = userRepository.findByEmail("locador@email.com").orElseThrow();

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

        List<Property> allProperties = propertyRepository.findAll();
        propertyToTest = allProperties.get(0);
    }

    @Test
    @DisplayName("Deve permitir que um LOCADOR crie um imóvel com sucesso")
    void createProperty_ShouldSucceed_WhenUserIsLocador() throws Exception {
        PropertyCreateDto propertyDto = new PropertyCreateDto();
        propertyDto.setTitle("Nova Casa de Teste");
        propertyDto.setDescription("Descrição completa da nova casa de teste.");
        propertyDto.setType(PropertyType.CASA);
        propertyDto.setRentValue(2000.00);
        propertyDto.setRooms(3);
        propertyDto.setBathrooms(2);
        propertyDto.setHasGarage(true);
        propertyDto.setIsFurnished(false);
        propertyDto.setPhotoUrls(Collections.emptyList());
        propertyDto.setApproximateLocation("Bairro Teste");

        mockMvc.perform(post("/api/properties")
                        .with(user(locadorOwner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar todos os imóveis ativos quando nenhum filtro é aplicado")
    void searchProperties_ShouldReturnAllActiveProperties_WhenNoFilterIsApplied() throws Exception {
        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

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

