package com.alugaserra.service;

import com.alugaserra.model.Chat;
import com.alugaserra.model.ChatMessage;
import com.alugaserra.model.Property;
import com.alugaserra.model.User;
import com.alugaserra.repository.ChatMessageRepository;
import com.alugaserra.repository.ChatRepository;
import com.alugaserra.repository.PropertyRepository;
import com.alugaserra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    @Autowired private ChatRepository chatRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PropertyRepository propertyRepository;

    @Transactional
    public Chat findOrCreateChat(UUID propertyId, UUID initiatorId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));
        User initiator = userRepository.findById(initiatorId).orElseThrow(() -> new RuntimeException("Usuário iniciador não encontrado"));
        User owner = property.getOwner();

        // Procura por um chat existente entre o iniciador e o dono do imóvel.
        return chatRepository.findChatByParticipantsAndProperty(initiator.getId(), owner.getId(), property.getId())
                .orElseGet(() -> {
                    // Se não existir, cria um novo chat.
                    Chat newChat = new Chat();
                    newChat.setProperty(property);
                    newChat.setParticipants(Arrays.asList(initiator, owner));
                    return chatRepository.save(newChat);
                });
    }

    public List<Chat> getUserChats(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return chatRepository.findByParticipantsContains(user);
    }

    public List<ChatMessage> getChatMessages(UUID chatId, UUID userId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat não encontrado"));

        // Validação de segurança: o usuário que pede as mensagens faz parte da conversa?
        boolean isParticipant = chat.getParticipants().stream().anyMatch(p -> p.getId().equals(userId));
        if (!isParticipant) {
            throw new AccessDeniedException("Usuário não tem permissão para aceder a este chat.");
        }

        return chatMessageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }
}
