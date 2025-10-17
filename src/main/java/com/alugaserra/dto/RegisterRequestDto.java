package com.alugaserra.dto;

import com.alugaserra.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "O nome não pode estar em branco")
        String name,

        @NotBlank(message = "O email não pode estar em branco")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "A senha não pode estar em branco")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String password,

        // --- CAMPOS ADICIONADOS ---
        @NotBlank(message = "O CPF não pode estar em branco")
        String cpf,

        @NotBlank(message = "O telefone не pode estar em branco")
        String phone,
        // --- FIM DA ADIÇÃO ---

        @NotNull(message = "O papel do usuário é obrigatório")
        UserRole role
) {
}