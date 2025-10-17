package com.alugaserra.repository;

import com.alugaserra.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    // Método para encontrar um plano pelo nome (ex: "Bronze")
    Optional<Plan> findByName(String name);
}
