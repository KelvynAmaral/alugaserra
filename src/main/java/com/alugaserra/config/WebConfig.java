package com.alugaserra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Classe de configuração para o CORS (Cross-Origin Resource Sharing).
 * Essencial para permitir que o frontend (), que corre numa porta diferente,
 * ou a página do Swagger, consigam fazer requisições para o nosso backend (Spring Boot).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Permite CORS para todos os endpoints que começam com /api/
                .allowedOrigins("http://localhost:5173", "http://localhost:8080") // Endereços permitidos (frontend e o próprio swagger)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(true);
    }
}
