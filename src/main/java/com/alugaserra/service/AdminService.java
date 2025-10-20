package com.alugaserra.service;

import com.alugaserra.dto.UserAdminViewDto;
import com.alugaserra.enums.UserRole;
import com.alugaserra.model.User;
import com.alugaserra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Contém a lógica de negócio para operações realizadas por administradores.
 */
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Atualiza o papel (role) de um usuário específico.
     * @param userId O ID do usuário a ser atualizado.
     * @param newRole O novo papel a ser atribuído.
     * @return O DTO do usuário atualizado.
     */
    @Transactional
    public UserAdminViewDto updateUserRole(UUID userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + userId));

        user.setRole(newRole);

        User updatedUser = userRepository.save(user);

        return new UserAdminViewDto(updatedUser);
    }
}
