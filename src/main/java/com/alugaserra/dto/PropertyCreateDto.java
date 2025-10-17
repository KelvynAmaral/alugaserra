package com.alugaserra.dto;

import com.alugaserra.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * DTO (Data Transfer Object) usado para receber os dados
 * da requisição de criação de um novo imóvel.
 */
@Data
public class PropertyCreateDto {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    @NotBlank(message = "A descrição é obrigatória")
    private String description;

    @NotNull(message = "O tipo de imóvel é obrigatório")
    private PropertyType type;

    @NotNull(message = "O valor do aluguel é obrigatório")
    @Positive(message = "O valor do aluguel deve ser positivo")
    private Double rentValue;

    @NotNull(message = "O número de quartos é obrigatório")
    private Integer rooms;

    @NotNull(message = "O número de banheiros é obrigatório")
    private Integer bathrooms;

    @NotNull(message = "A informação sobre garagem é obrigatória")
    private Boolean hasGarage;

    @NotNull(message = "A informação sobre mobília é obrigatória")
    private Boolean isFurnished;

    // Em uma versão futura, trataremos o upload. Por enquanto, recebemos as URLs.
    private List<String> photoUrls;

    private String videoUrl;

    @NotBlank(message = "A localização aproximada é obrigatória")
    private String approximateLocation;
}