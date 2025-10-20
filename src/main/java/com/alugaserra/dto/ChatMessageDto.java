package com.alugaserra.dto;

import lombok.Data;

import java.util.UUID;

/**
 * DTO para transportar informações de uma mensagem de chat.
 * Usado para a comunicação entre o cliente e o servidor via WebSocket.
 */
@Data
public class ChatMessageDto {
    private UUID chatId;
    private UUID senderId;
    private UUID recipientId; // Precisamos de saber para quem enviar a mensagem
    private String content;
}
