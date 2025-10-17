package com.alugaserra.dto;

import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PropertyUpdateDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull PropertyType type,
        @NotNull PropertyStatus status, // Permite alterar o status (ex: para PAUSED ou RENTED)
        @NotNull @Positive double rentValue,
        @NotNull @Positive int rooms,
        @NotNull @Positive int bathrooms,
        @NotNull Boolean hasGarage,
        @NotNull Boolean isFurnished,
        List<String> photoUrls, // Permite atualizar a lista de fotos
        String videoUrl,
        @NotBlank String approximateLocation
) {
}
