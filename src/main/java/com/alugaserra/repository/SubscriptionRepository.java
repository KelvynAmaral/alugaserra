package com.alugaserra.repository;

import com.alugaserra.model.Subscription;
import com.alugaserra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    // Método para encontrar a assinatura de um usuário específico
    Optional<Subscription> findByUser(User user);
}