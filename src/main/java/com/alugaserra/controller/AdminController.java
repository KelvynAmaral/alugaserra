package com.alugaserra.controller;

import com.alugaserra.dto.UserAdminViewDto;
import com.alugaserra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
}
