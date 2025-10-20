package com.alugaserra.controller;

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
     * @return Uma lista de usuários com dados selecionados para a visão do admin.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminViewDto>> getAllUsers() {
        List<UserAdminViewDto> users = userRepository.findAll()
                .stream()
                .map(UserAdminViewDto::new) // Converte cada User para o DTO
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint para atualizar o papel de um usuário.
     * @param userId O ID do usuário a ser modificado.
     * @param newRole O novo papel (enviado como um parâmetro de requisição).
     * @return Os dados atualizados do usuário.
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserAdminViewDto> updateUserRole(@PathVariable UUID userId, @RequestParam UserRole newRole) {
        UserAdminViewDto updatedUser = adminService.updateUserRole(userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }
}

