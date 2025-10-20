package com.alugaserra.controller;

import com.alugaserra.dto.PropertyResponseDto;
import com.alugaserra.dto.UserAdminViewDto;
import com.alugaserra.enums.UserRole;
import com.alugaserra.repository.UserRepository;
import com.alugaserra.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller para funcionalidades exclusivas do administrador.
 * Todas as rotas aqui dentro requerem o papel 'ADMIN'.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminService adminService;

    /**
     * Endpoint para listar todos os usuários cadastrados na plataforma.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminViewDto>> getAllUsers() {
        List<UserAdminViewDto> users = userRepository.findAll()
                .stream()
                .map(UserAdminViewDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint para atualizar o papel de um usuário.
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserAdminViewDto> updateUserRole(@PathVariable UUID userId, @RequestParam UserRole newRole) {
        UserAdminViewDto updatedUser = adminService.updateUserRole(userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    // --- NOVOS ENDPOINTS ---

    /**
     * Endpoint para o admin listar TODOS os imóveis da plataforma.
     * @return Uma lista de todos os imóveis, independentemente do status.
     */
    @GetMapping("/properties")
    public ResponseEntity<List<PropertyResponseDto>> getAllPropertiesForAdmin() {
        List<PropertyResponseDto> properties = adminService.findAllProperties();
        return ResponseEntity.ok(properties);
    }

    /**
     * Endpoint para o admin deletar qualquer imóvel (ação de moderação).
     * @param propertyId O ID do imóvel a ser deletado.
     * @return Resposta 204 No Content em caso de sucesso.
     */
    @DeleteMapping("/properties/{propertyId}")
    public ResponseEntity<Void> deletePropertyByAdmin(@PathVariable UUID propertyId) {
        adminService.deleteProperty(propertyId);
        return ResponseEntity.noContent().build();
    }
}

