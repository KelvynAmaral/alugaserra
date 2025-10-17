package com.alugaserra.repository;

import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.model.Property;
import com.alugaserra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;



public interface PropertyRepository extends JpaRepository<Property, UUID> {
    // Método para buscar todas as propriedades por um status específico
    List<Property> findByStatus(PropertyStatus status);

    // Método para contar quantos imóveis ativos um usuário específico possui.
    long countByOwnerAndStatus(User owner, PropertyStatus status);
}

