package com.alugaserra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuração central para o WebSocket.
 * Habilita o message broker e define os endpoints e prefixos para a comunicação STOMP.
 */
@Configuration
@EnableWebSocketMessageBroker // Habilita o processamento de mensagens via WebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configura o message broker que envia mensagens do servidor para o cliente.
        // /topic: para mensagens em broadcast (ex: chats em grupo)
        // /user: para mensagens diretas para um usuário específico
        config.enableSimpleBroker("/topic", "/user");

        // Define o prefixo para os endpoints que recebem mensagens do cliente.
        // Ex: um cliente enviará uma mensagem para /app/chat.send
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra o endpoint de conexão principal para o WebSocket.
        // É a "porta de entrada" do túnel de comunicação.
        registry.addEndpoint("/ws")
                // Permite que o nosso frontend React (rodando em localhost:5173) se conecte.
                .setAllowedOrigins("http://localhost:5173")
                // withSockJS() fornece uma alternativa (fallback) para navegadores que não suportam WebSocket.
                .withSockJS();
    }
}
