package com.alugaserra.repository;

import com.alugaserra.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    // Futuramente, adicionaremos métodos de busca aqui.
}
