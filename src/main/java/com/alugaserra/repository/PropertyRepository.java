package com.alugaserra.repository;

import com.alugaserra.enums.PropertyStatus;
import com.alugaserra.model.Property;
import com.alugaserra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- 1. IMPORTAR

import java.util.List;
import java.util.UUID;


// 2. ADICIONAR JpaSpecificationExecutor
public interface PropertyRepository extends JpaRepository<Property, UUID>, JpaSpecificationExecutor<Property> {

    List<Property> findByStatus(PropertyStatus status);

    long countByOwnerAndStatus(User owner, PropertyStatus status);
}
