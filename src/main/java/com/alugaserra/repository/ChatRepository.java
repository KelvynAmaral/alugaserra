package com.alugaserra.repository;

import com.alugaserra.model.Chat;
import com.alugaserra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {

    // Procura por um chat que contenha exatamente dois participantes específicos para um determinado imóvel.
    @Query("SELECT c FROM Chat c JOIN c.participants p1 JOIN c.participants p2 WHERE c.property.id = :propertyId AND p1.id = :userId1 AND p2.id = :userId2")
    Optional<Chat> findChatByParticipantsAndProperty(@Param("userId1") UUID userId1, @Param("userId2") UUID userId2, @Param("propertyId") UUID propertyId);

    // Encontra todos os chats nos quais um determinado usuário é participante.
    List<Chat> findByParticipantsContains(User user);
}

