package com.alugaserra.repository;

import com.alugaserra.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
    // Método para encontrar um plano pelo nome (ex: "Bronze")
    Optional<Plan> findByName(String name);
}
