package com.alugaserra.model;

import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.enums.PropertyType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Data
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private PropertyType type; // CASA, APARTAMENTO, BARRACAO, KITNET, QUARTO

    private double rentValue;
    private int rooms;
    private int bathrooms;
    private boolean hasGarage;
    private boolean isFurnished;

    @ElementCollection // Para armazenar URLs das fotos
    private List<String> photoUrls;

    private String videoUrl;

    private String approximateLocation; // Bairro ou ponto de referência

    @Enumerated(EnumType.STRING)
    private PropertyStatus status = PropertyStatus.ACTIVE; // ACTIVE, PAUSED, RENTED

    // --- CAMPO ADICIONADO ---
    @CreationTimestamp // Define automaticamente a data e hora no momento da criação
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}