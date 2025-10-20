package com.alugaserra.controller;

import com.alugaserra.dto.ChatMessageDto;
import com.alugaserra.model.Chat;
import com.alugaserra.model.ChatMessage;
import com.alugaserra.model.User;
import com.alugaserra.repository.ChatMessageRepository;
import com.alugaserra.repository.ChatRepository;
import com.alugaserra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

/**
 * Controller para lidar com as mensagens de chat em tempo real via WebSocket.
 */
@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Ferramenta para enviar mensagens

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint para receber e processar o envio de uma nova mensagem.
     * Os clientes enviam mensagens para o destino "/app/chat.sendMessage".
     *
     * @param chatMessageDto O DTO contendo os dados da mensagem.
     */
    @MessageMapping("/chat.sendMessage")
    @Transactional
    public void sendMessage(ChatMessageDto chatMessageDto) {
        // 1. Busca as entidades no banco de dados
        Chat chat = chatRepository.findById(chatMessageDto.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));
        User sender = userRepository.findById(chatMessageDto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Remetente não encontrado"));

        // 2. Cria a entidade da mensagem
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChat(chat);
        chatMessage.setSender(sender);
        chatMessage.setContent(chatMessageDto.getContent());

        // 3. Salva a mensagem no banco de dados
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // 4. Envia a mensagem para o destinatário correto via WebSocket.
        // O destino será algo como "/user/UUID_DO_DESTINATARIO/queue/messages".
        // O frontend precisará de estar "inscrito" neste tópico para receber a mensagem.
        messagingTemplate.convertAndSendToUser(
                chatMessageDto.getRecipientId().toString(), // ID do destinatário
                "/queue/messages", // Tópico privado
                savedMessage // Envia a mensagem completa salva
        );
    }
}
