package com.alugaserra.controller;

import com.alugaserra.dto.PropertyCreateDto;
import com.alugaserra.dto.PropertyResponseDto;
import com.alugaserra.dto.PropertyUpdateDto;
import com.alugaserra.enums.PropertyType; // <-- Importar
import com.alugaserra.model.User;
import com.alugaserra.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    // **** MÉTODO GET ATUALIZADO PARA ACEITAR FILTROS ****
    @GetMapping
    public ResponseEntity<List<PropertyResponseDto>> getAllProperties(
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) Double maxRent,
            @RequestParam(required = false) Integer minRooms,
            @RequestParam(required = false) Boolean hasGarage
    ) {
        var properties = propertyService.searchProperties(type, maxRent, minRooms, hasGarage);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable UUID id) {
        var propertyDto = propertyService.findPropertyById(id);
        return ResponseEntity.ok(propertyDto);
    }

    @PostMapping
    public ResponseEntity<PropertyResponseDto> createProperty(@RequestBody @Valid PropertyCreateDto propertyDto, @AuthenticationPrincipal User currentUser, UriComponentsBuilder uriBuilder) {
        PropertyResponseDto responseDto = propertyService.createProperty(propertyDto, currentUser);
        var uri = uriBuilder.path("/api/properties/{id}").buildAndExpand(responseDto.id()).toUri();
        return ResponseEntity.created(uri).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> updateProperty(@PathVariable UUID id, @RequestBody @Valid PropertyUpdateDto propertyDto, @AuthenticationPrincipal User currentUser) {
        PropertyResponseDto updatedProperty = propertyService.updateProperty(id, propertyDto, currentUser);
        return ResponseEntity.ok(updatedProperty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        propertyService.deleteProperty(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}

