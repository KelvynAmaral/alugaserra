package com.alugaserra.repository;

import com.alugaserra.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * Encontra todas as mensagens de um chat específico, ordenadas pela data de envio (timestamp).
     * O Spring Data JPA cria esta consulta automaticamente.
     */
    List<ChatMessage> findByChatIdOrderByTimestampAsc(UUID chatId);
}

