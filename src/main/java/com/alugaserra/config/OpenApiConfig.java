package com.alugaserra.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração para a documentação da API com SpringDoc (OpenAPI/Swagger).
 * Define as informações gerais da API e configura o esquema de segurança para JWT.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AlugaSerra API",
                version = "v1.0",
                description = "API RESTful para a plataforma de aluguel de imóveis AlugaSerra."
        ),
        security = @SecurityRequirement(name = "bearerAuth") // Aplica a segurança globalmente
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Insira o token JWT obtido no endpoint de login."
)
public class OpenApiConfig {
}
