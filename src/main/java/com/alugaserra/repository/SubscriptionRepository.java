package com.alugaserra.repository;

import com.alugaserra.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    // Método para encontrar a assinatura de um usuário específico pelo seu ID (mais otimizado)
    Optional<Subscription> findByUser_Id(UUID userId);
}
