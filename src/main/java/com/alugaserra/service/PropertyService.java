package com.alugaserra.service;

import com.alugaserra.dto.OwnerSummaryDto;
import com.alugaserra.dto.PropertyCreateDto;
import com.alugaserra.dto.PropertyResponseDto;
import com.alugaserra.dto.PropertyUpdateDto;
import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.model.Property;
import com.alugaserra.model.User;
import com.alugaserra.repository.PropertyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Contém a lógica de negócio para operações relacionadas a imóveis.
 */
@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    // --- Métodos de Leitura (Públicos) ---

    public List<PropertyResponseDto> findAllActiveProperties() {
        return propertyRepository.findByStatus(PropertyStatus.ACTIVE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PropertyResponseDto findPropertyById(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado com o ID: " + id));
        return convertToDto(property);
    }

    // --- Métodos de Escrita (Protegidos) ---

    public Property createProperty(PropertyCreateDto dto, User owner) {
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
        return propertyRepository.save(newProperty);
    }

    public PropertyResponseDto updateProperty(UUID id, PropertyUpdateDto dto, User currentUser) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Imóvel não encontrado com o ID: " + id));

        // Verificação de segurança crucial: o usuário atual é o dono do imóvel?
        if (!property.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Usuário não tem permissão para alterar este imóvel.");
        }

        // Atualiza os campos do imóvel com os dados do DTO
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

        // Verificação de segurança crucial
        if (!property.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Usuário não tem permissão para deletar este imóvel.");
        }

        propertyRepository.delete(property);
    }

    // --- Método Auxiliar ---

    private PropertyResponseDto convertToDto(Property property) {
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
