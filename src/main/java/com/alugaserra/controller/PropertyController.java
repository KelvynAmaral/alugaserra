package com.alugaserra.controller;

import com.alugaserra.dto.PropertyCreateDto;
import com.alugaserra.dto.PropertyResponseDto;
import com.alugaserra.dto.PropertyUpdateDto;
import com.alugaserra.model.Property;
import com.alugaserra.model.User;
import com.alugaserra.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    /**
     * Endpoint para listar todos os imóveis ativos. Acessível publicamente.
     */
    @GetMapping
    public ResponseEntity<List<PropertyResponseDto>> getAllProperties() {
        var properties = propertyService.findAllActiveProperties();
        return ResponseEntity.ok(properties);
    }

    /**
     * Endpoint para buscar um imóvel específico pelo seu ID. Acessível publicamente.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable UUID id) {
        var propertyDto = propertyService.findPropertyById(id);
        return ResponseEntity.ok(propertyDto);
    }

    /**
     * Endpoint para criar um novo imóvel. Requer autenticação de um usuário com o papel de LOCADOR.
     */
    @PostMapping
    public ResponseEntity<PropertyResponseDto> createProperty(@RequestBody @Valid PropertyCreateDto propertyDto, Authentication authentication, UriComponentsBuilder uriBuilder) {
        // Pega o usuário autenticado que está fazendo a requisição
        User currentUser = (User) authentication.getPrincipal();
        Property createdProperty = propertyService.createProperty(propertyDto, currentUser);

        var uri = uriBuilder.path("/api/properties/{id}").buildAndExpand(createdProperty.getId()).toUri();

        // Converte a entidade salva para o DTO de resposta para retornar ao cliente
        PropertyResponseDto responseDto = propertyService.findPropertyById(createdProperty.getId());

        return ResponseEntity.created(uri).body(responseDto);
    }

    /**
     * Endpoint para atualizar um imóvel existente.
     * Requer que o usuário seja um LOCADOR e o proprietário do imóvel.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> updateProperty(@PathVariable UUID id, @RequestBody @Valid PropertyUpdateDto propertyDto, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        PropertyResponseDto updatedProperty = propertyService.updateProperty(id, propertyDto, currentUser);
        return ResponseEntity.ok(updatedProperty);
    }

    /**
     * Endpoint para deletar um imóvel.
     * Requer que o usuário seja um LOCADOR e o proprietário do imóvel.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        propertyService.deleteProperty(id, currentUser);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content, que é o padrão para delete
    }
}