package com.alugaserra.exception;

import com.alugaserra.dto.ApiErrorDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Classe centralizada para tratamento de exceções em toda a aplicação.
 * Captura exceções específicas e as transforma em respostas HTTP padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata erros de validação de DTOs (ex: @NotBlank, @NotNull)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiErrorDto errorDto = new ApiErrorDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erro de validação: " + errors,
                request.getRequestURI());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    // Trata erros de regras de negócio (ex: limite de plano atingido)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(), // 409 Conflict é um bom status para regras de negócio
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    // Trata erros quando uma entidade não é encontrada no banco de dados
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    // Trata erros de permissão (ex: inquilino tentando alterar imóvel)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> handleAccessDeniedException(HttpServletRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "Acesso negado. Você não tem permissão para realizar esta operação.",
                request.getRequestURI());
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }

    // Um handler genérico para qualquer outra exceção não tratada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericException(HttpServletRequest request) {
        ApiErrorDto errorDto = new ApiErrorDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocorreu um erro inesperado no servidor.",
                request.getRequestURI());

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
