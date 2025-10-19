package com.alugaserra.service;

import com.alugaserra.dto.PropertyCreateDto;
import com.alugaserra.dto.PropertyResponseDto;
import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.enums.PropertyType;
import com.alugaserra.model.Plan;
import com.alugaserra.model.Property;
import com.alugaserra.model.Subscription;
import com.alugaserra.model.User;
import com.alugaserra.repository.PropertyRepository;
import com.alugaserra.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Inicia o Mockito para este teste
@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    // Cria "dublês" (Mocks) dos repositórios. Eles não acedem à base de dados.
    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    // Cria uma instância real do PropertyService e injeta os Mocks acima nela.
    @InjectMocks
    private PropertyService propertyService;

    // Variáveis de teste que serão usadas em múltiplos testes
    private User locador;
    private Plan planBronze;
    private Subscription subscriptionAtiva;
    private PropertyCreateDto propertyCreateDto;

    @BeforeEach
    void setUp() {
        // --- Prepara os nossos dados de teste ---
        locador = new User();
        locador.setId(UUID.randomUUID());
        locador.setName("Teste Locador");
        locador.setPhone("31999998888");

        planBronze = new Plan();
        planBronze.setName("Bronze");
        planBronze.setMaxProperties(1);

        subscriptionAtiva = new Subscription();
        subscriptionAtiva.setUser(locador);
        subscriptionAtiva.setPlan(planBronze);
        subscriptionAtiva.setStatus("ACTIVE");
        subscriptionAtiva.setStartDate(LocalDate.now().minusMonths(1));
        subscriptionAtiva.setEndDate(LocalDate.now().plusMonths(1));

        // **** CORREÇÃO AQUI: Inicializa o DTO com dados válidos ****
        propertyCreateDto = new PropertyCreateDto();
        propertyCreateDto.setTitle("Casa Aconchegante");
        propertyCreateDto.setDescription("Ótima casa para família.");
        propertyCreateDto.setType(PropertyType.CASA);
        propertyCreateDto.setRentValue(1200.00);
        propertyCreateDto.setRooms(2);
        propertyCreateDto.setBathrooms(1);
        propertyCreateDto.setHasGarage(true);
        propertyCreateDto.setIsFurnished(false);
        propertyCreateDto.setPhotoUrls(Collections.emptyList());
        propertyCreateDto.setApproximateLocation("Bairro X");
    }

    @Test
    @DisplayName("Deve criar um imóvel com sucesso quando o limite do plano não for atingido")
    void createProperty_ShouldSucceed_WhenPlanLimitIsNotReached() {
        // --- Configuração dos Mocks (Arrange) ---
        Property savedProperty = new Property();
        savedProperty.setId(UUID.randomUUID());
        savedProperty.setOwner(locador);
        savedProperty.setCreatedAt(LocalDateTime.now());

        when(subscriptionRepository.findByUser_Id(locador.getId())).thenReturn(Optional.of(subscriptionAtiva));
        when(propertyRepository.countByOwnerAndStatus(locador, PropertyStatus.ACTIVE)).thenReturn(0L);
        when(propertyRepository.save(any(Property.class))).thenReturn(savedProperty);

        // --- Execução da lógica (Act) ---
        PropertyResponseDto resultDto = propertyService.createProperty(propertyCreateDto, locador);

        // --- Verificação (Assert) ---
        assertNotNull(resultDto);
        assertEquals(savedProperty.getId(), resultDto.id());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar imóvel quando o limite do plano for atingido")
    void createProperty_ShouldThrowException_WhenPlanLimitIsReached() {
        // --- Configuração dos Mocks (Arrange) ---
        when(subscriptionRepository.findByUser_Id(locador.getId())).thenReturn(Optional.of(subscriptionAtiva));
        when(propertyRepository.countByOwnerAndStatus(locador, PropertyStatus.ACTIVE)).thenReturn(1L);

        // --- Execução e Verificação (Act & Assert) ---
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            propertyService.createProperty(propertyCreateDto, locador);
        });

        assertEquals("Você atingiu o limite de 1 imóvel(is) do seu plano 'Bronze'.", exception.getMessage());
        verify(propertyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar imóvel com uma assinatura inativa")
    void createProperty_ShouldThrowException_WhenSubscriptionIsInactive() {
        // --- Configuração dos Mocks (Arrange) ---
        subscriptionAtiva.setStatus("INACTIVE");
        when(subscriptionRepository.findByUser_Id(locador.getId())).thenReturn(Optional.of(subscriptionAtiva));

        // --- Execução e Verificação (Act & Assert) ---
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            propertyService.createProperty(propertyCreateDto, locador);
        });

        assertEquals("A sua assinatura não está ativa. Por favor, regularize para cadastrar novos imóveis.", exception.getMessage());
        verify(propertyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o utilizador não tem assinatura")
    void createProperty_ShouldThrowException_WhenUserHasNoSubscription() {
        // --- Configuração dos Mocks (Arrange) ---
        when(subscriptionRepository.findByUser_Id(locador.getId())).thenReturn(Optional.empty());

        // --- Execução e Verificação (Act & Assert) ---
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            propertyService.createProperty(propertyCreateDto, locador);
        });

        assertEquals("Usuário não possui uma assinatura ativa para cadastrar imóveis.", exception.getMessage());
        verify(propertyRepository, never()).save(any());
    }
}

