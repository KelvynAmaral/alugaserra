package com.alugaserra.controller;

import com.alugaserra.config.DataInitializer;
import com.alugaserra.dto.PropertyCreateDto;
import com.alugaserra.enums.PropertyType;
import com.alugaserra.enums.UserRole;
import com.alugaserra.model.User;
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

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PropertyControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private DataInitializer dataInitializer; // Injeta o nosso inicializador

    private User locador;
    private User inquilino;

    @BeforeEach
    void setupDatabase() {
        // Força a execução do DataInitializer para garantir que os dados de teste existam
        try {
            dataInitializer.run(new String[]{});
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar dados de teste", e);
        }

        locador = userRepository.findByEmail("locador@email.com").orElseThrow();

        inquilino = new User();
        inquilino.setEmail("inquilino.teste@email.com");
        inquilino.setPasswordHash(passwordEncoder.encode("password"));
        inquilino.setRole(UserRole.INQUILINO);
        userRepository.save(inquilino);
    }

    // --- TESTES DE CRIAÇÃO (POST) ---

    @Test
    @DisplayName("Deve permitir que um LOCADOR crie um imóvel com sucesso")
    void createProperty_ShouldSucceed_WhenUserIsLocador() throws Exception {
        // Preenche todos os campos obrigatórios do DTO para passar na validação
        PropertyCreateDto propertyDto = new PropertyCreateDto();
        propertyDto.setTitle("Nova Casa de Teste");
        propertyDto.setDescription("Descrição completa da nova casa de teste.");
        propertyDto.setType(PropertyType.CASA);
        propertyDto.setRentValue(2000.00);
        propertyDto.setRooms(3);
        propertyDto.setBathrooms(2);
        propertyDto.setHasGarage(true);
        propertyDto.setIsFurnished(false); // Campo obrigatório
        propertyDto.setPhotoUrls(Collections.emptyList());
        propertyDto.setApproximateLocation("Bairro Teste"); // Campo obrigatório

        mockMvc.perform(post("/api/properties")
                        .with(user(locador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve proibir que um INQUILINO crie um imóvel")
    void createProperty_ShouldFail_WhenUserIsInquilino() throws Exception {
        PropertyCreateDto propertyDto = new PropertyCreateDto();
        // Preencher o DTO não é estritamente necessário aqui, pois a segurança deve barrar antes.
        // Mas é uma boa prática para evitar erros de validação caso a segurança falhe.
        propertyDto.setTitle("Tentativa de Criação");
        propertyDto.setDescription("...");
        propertyDto.setType(PropertyType.QUARTO);
        propertyDto.setRentValue(500.0);
        propertyDto.setRooms(1);
        propertyDto.setBathrooms(1);
        propertyDto.setHasGarage(false);
        propertyDto.setIsFurnished(true);
        propertyDto.setApproximateLocation("Centro");

        mockMvc.perform(post("/api/properties")
                        .with(user(inquilino)) // Simula a requisição como um INQUILINO
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyDto)))
                .andExpect(status().isForbidden()); // Esperamos o status 403 Forbidden (Acesso Negado)
    }

    // --- NOVOS TESTES DE BUSCA (GET) COM FILTROS ---

    @Test
    @DisplayName("Deve retornar todos os imóveis ativos quando nenhum filtro é aplicado")
    void searchProperties_ShouldReturnAllActiveProperties_WhenNoFilterIsApplied() throws Exception {
        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))); // Espera os 3 imóveis do DataInitializer
    }

    @Test
    @DisplayName("Deve retornar apenas imóveis do tipo CASA quando o filtro 'type' é usado")
    void searchProperties_ShouldReturnOnlyCasas_WhenTypeFilterIsCasa() throws Exception {
        mockMvc.perform(get("/api/properties").param("type", "CASA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Casa Grande com Quintal"));
    }

    @Test
    @DisplayName("Deve retornar imóveis com aluguel até 1500 quando o filtro 'maxRent' é usado")
    void searchProperties_ShouldReturnProperties_WhenMaxRentFilterIsApplied() throws Exception {
        mockMvc.perform(get("/api/properties").param("maxRent", "1500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))); // Apartamento (1200) e Kitnet (800)
    }

    @Test
    @DisplayName("Deve combinar filtros de tipo e quartos corretamente")
    void searchProperties_ShouldCombineFiltersCorrectly() throws Exception {
        mockMvc.perform(get("/api/properties")
                        .param("type", "CASA")
                        .param("minRooms", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Casa Grande com Quintal"));
    }
}

