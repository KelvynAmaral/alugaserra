package com.alugaserra.dto;

import com.alugaserra.enums.UserRole;
import com.alugaserra.model.User;

import java.util.UUID;

/**
 * DTO para exibir informações de um usuário na perspectiva de um administrador.
 * Omite dados sensíveis como o hash da senha.
 */
public record UserAdminViewDto(
        UUID id,
        String name,
        String email,
        String cpf,
        String phone,
        UserRole role,
        boolean verified
) {
    // Construtor de conveniência para converter a entidade User para este DTO.
    public UserAdminViewDto(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getPhone(),
                user.getRole(),
                user.isVerified()
        );
    }
}
