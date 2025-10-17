package com.alugaserra.dto;

import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.enums.PropertyType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PropertyResponseDto(
        UUID id,
        String title,
        String description,
        PropertyType type,
        PropertyStatus status,
        double rentValue,
        int rooms,
        int bathrooms,
        boolean hasGarage,
        boolean isFurnished,
        List<String> photoUrls,
        String videoUrl,
        String approximateLocation,
        OwnerSummaryDto owner,
        LocalDateTime createdAt // <-- Campo adicionado
) {
}
