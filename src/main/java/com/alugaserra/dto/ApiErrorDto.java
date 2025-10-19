package com.alugaserra.dto;

import java.time.LocalDateTime;

/**
 * DTO padrão para respostas de erro da API.
 */
public record ApiErrorDto(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
}
