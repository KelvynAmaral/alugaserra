package com.alugaserra.dto;

import com.alugaserra.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF; // <-- 1. IMPORTAR A ANOTAÇÃO CORRETA

public record RegisterRequestDto(
        @NotBlank(message = "O nome não pode estar em branco")
        String name,

        @NotBlank(message = "O email não pode estar em branco")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "A senha não pode estar em branco")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String password,

        @NotBlank(message = "O CPF não pode estar em branco")
        @CPF(message = "CPF inválido")
        String cpf,

        @NotBlank(message = "O telefone não pode estar em branco")
        String phone,

        @NotNull(message = "O papel do usuário é obrigatório")
        UserRole role
) {
}

