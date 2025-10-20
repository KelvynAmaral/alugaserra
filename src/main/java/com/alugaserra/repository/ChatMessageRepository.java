package com.alugaserra.repository;

import com.alugaserra.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    // Futuramente, adicionaremos métodos de busca aqui.
}
