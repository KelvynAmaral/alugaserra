package com.alugaserra.service;

import com.alugaserra.dto.OwnerSummaryDto;
import com.alugaserra.dto.PropertyCreateDto;
import com.alugaserra.dto.PropertyResponseDto;
import com.alugaserra.dto.PropertyUpdateDto;
import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.enums.PropertyType; // <-- Importar
import com.alugaserra.model.Property;
import com.alugaserra.model.Subscription;
import com.alugaserra.model.User;
import com.alugaserra.repository.PropertyRepository;
import com.alugaserra.repository.SubscriptionRepository;
import com.alugaserra.repository.specification.PropertySpecification; // <-- Importar
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification; // <-- Importar
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // --- MÉTODOS DE BUSCA ATUALIZADOS ---

    // Este método agora é um atalho para a busca com filtros, sem nenhum filtro aplicado.
    public List<PropertyResponseDto> findAllActiveProperties() {
        return searchProperties(null, null, null, null);
    }

    // **** NOVO MÉTODO PARA BUSCA DINÂMICA ****
    public List<PropertyResponseDto> searchProperties(PropertyType type, Double maxRent, Integer minRooms, Boolean hasGarage) {
        // Começamos com a especificação base: apenas imóveis ativos.
        Specification<Property> spec = Specification.where(PropertySpecification.isActive());

        // Adicionamos os filtros à especificação apenas se eles foram fornecidos.
        if (type != null) {
            spec = spec.and(PropertySpecification.hasType(type));
        }
        if (maxRent != null) {
            spec = spec.and(PropertySpecification.maxRent(maxRent));
        }
        if (minRooms != null) {
            spec = spec.and(PropertySpecification.minRooms(minRooms));
        }
        if (hasGarage != null && hasGarage) {
            spec = spec.and(PropertySpecification.hasGarage());
        }

        // Executamos a consulta com todos os filtros combinados.
        return propertyRepository.findAll(spec)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public PropertyResponseDto findPropertyById(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado com o ID: " + id));
        return convertToDto(property);
    }

    // --- O restante da classe (create, update, delete, etc.) continua igual ---
    public PropertyResponseDto createProperty(PropertyCreateDto dto, User owner) {
        Subscription subscription = subscriptionRepository.findByUser_Id(owner.getId())
                .orElseThrow(() -> new IllegalStateException("Usuário não possui uma assinatura ativa para cadastrar imóveis."));

        if (!"ACTIVE".equals(subscription.getStatus()) || (subscription.getEndDate() != null && subscription.getEndDate().isBefore(LocalDate.now()))) {
            throw new IllegalStateException("A sua assinatura não está ativa. Por favor, regularize para cadastrar novos imóveis.");
        }

        long currentPropertyCount = propertyRepository.countByOwnerAndStatus(owner, PropertyStatus.ACTIVE);
        int maxPropertiesAllowed = subscription.getPlan().getMaxProperties();

        if (currentPropertyCount >= maxPropertiesAllowed) {
            throw new IllegalStateException("Você atingiu o limite de " + maxPropertiesAllowed + " imóvel(is) do seu plano '" + subscription.getPlan().getName() + "'.");
        }

        Property newProperty = new Property();
        newProperty.setOwner(owner);
        newProperty.setTitle(dto.getTitle());
        newProperty.setDescription(dto.getDescription());
        newProperty.setType(dto.getType());
        newProperty.setRentValue(dto.getRentValue());
        newProperty.setRooms(dto.getRooms());
        newProperty.setBathrooms(dto.getBathrooms());
        newProperty.setHasGarage(dto.getHasGarage());
        newProperty.setFurnished(dto.getIsFurnished());
        newProperty.setPhotoUrls(dto.getPhotoUrls());
        newProperty.setVideoUrl(dto.getVideoUrl());
        newProperty.setApproximateLocation(dto.getApproximateLocation());

        Property savedProperty = propertyRepository.save(newProperty);

        return convertToDto(savedProperty);
    }

    public PropertyResponseDto updateProperty(UUID id, PropertyUpdateDto dto, User currentUser) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado com o ID: " + id));

        if (!property.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Usuário não tem permissão para alterar este imóvel.");
        }

        property.setTitle(dto.title());
        property.setDescription(dto.description());
        property.setType(dto.type());
        property.setStatus(dto.status());
        property.setRentValue(dto.rentValue());
        property.setRooms(dto.rooms());
        property.setBathrooms(dto.bathrooms());
        property.setHasGarage(dto.hasGarage());
        property.setFurnished(dto.isFurnished());
        property.setPhotoUrls(dto.photoUrls());
        property.setVideoUrl(dto.videoUrl());
        property.setApproximateLocation(dto.approximateLocation());

        Property updatedProperty = propertyRepository.save(property);
        return convertToDto(updatedProperty);
    }

    public void deleteProperty(UUID id, User currentUser) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado com o ID: " + id));

        if (!property.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Usuário não tem permissão para deletar este imóvel.");
        }

        propertyRepository.delete(property);
    }

    private PropertyResponseDto convertToDto(Property property) {
        if (property.getOwner() == null) {
            return null;
        }

        OwnerSummaryDto ownerDto = new OwnerSummaryDto(
                property.getOwner().getName(),
                property.getOwner().getPhone()
        );

        return new PropertyResponseDto(
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                property.getType(),
                property.getStatus(),
                property.getRentValue(),
                property.getRooms(),
                property.getBathrooms(),
                property.isHasGarage(),
                property.isFurnished(),
                property.getPhotoUrls(),
                property.getVideoUrl(),
                property.getApproximateLocation(),
                ownerDto,
                property.getCreatedAt()
        );
    }
}
