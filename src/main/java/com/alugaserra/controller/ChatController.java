package com.alugaserra.controller;

import com.alugaserra.dto.ChatMessageDto;
import com.alugaserra.model.Chat;
import com.alugaserra.model.ChatMessage;
import com.alugaserra.model.User;
import com.alugaserra.repository.ChatMessageRepository;
import com.alugaserra.repository.ChatRepository;
import com.alugaserra.repository.UserRepository;
import com.alugaserra.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RestController // Transforma este em um controller REST
@RequestMapping("/api/chats") // Define o prefixo para todos os endpoints REST
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService; // Injeta o nosso novo serviço

    // --- ENDPOINTS REST ---

    /**
     * Inicia uma nova conversa (ou encontra uma existente) entre o usuário logado e o dono de um imóvel.
     * @param propertyId O ID do imóvel que originou a conversa.
     * @param currentUser O usuário autenticado que está a iniciar o chat.
     * @return O objeto Chat criado ou encontrado.
     */
    @PostMapping("/start")
    public ResponseEntity<Chat> startChat(@RequestParam UUID propertyId, @AuthenticationPrincipal User currentUser) {
        Chat chat = chatService.findOrCreateChat(propertyId, currentUser.getId());
        return ResponseEntity.ok(chat);
    }

    /**
     * Retorna uma lista de todas as conversas em que o usuário logado participa.
     * @param currentUser O usuário autenticado.
     * @return Uma lista de chats.
     */
    @GetMapping
    public ResponseEntity<List<Chat>> getUserChats(@AuthenticationPrincipal User currentUser) {
        List<Chat> chats = chatService.getUserChats(currentUser.getId());
        return ResponseEntity.ok(chats);
    }

    /**
     * Retorna o histórico de mensagens de uma conversa específica.
     * @param chatId O ID do chat.
     * @param currentUser O usuário autenticado (para verificação de segurança).
     * @return Uma lista de mensagens.
     */
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable UUID chatId, @AuthenticationPrincipal User currentUser) {
        List<ChatMessage> messages = chatService.getChatMessages(chatId, currentUser.getId());
        return ResponseEntity.ok(messages);
    }

    // --- ENDPOINT WEBSOCKET ---

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
        messagingTemplate.convertAndSendToUser(
                chatMessageDto.getRecipientId().toString(), // ID do destinatário
                "/queue/messages", // Tópico privado
                savedMessage // Envia a mensagem completa salva
        );
    }
}

