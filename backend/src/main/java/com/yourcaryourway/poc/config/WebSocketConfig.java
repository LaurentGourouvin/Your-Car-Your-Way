package com.yourcaryourway.poc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration using STOMP protocol.
 * Configures the message broker and the WebSocket endpoint.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker.
     * Enables an in-memory broker for /topic and /queue destinations.
     * Sets /app as the prefix for messages routed to @MessageMapping methods.
     *
     * @param registry the message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP WebSocket endpoint.
     * Clients connect to /ws to establish a WebSocket connection.
     * SockJS is enabled as a fallback for browsers that don't support WebSocket.
     *
     * @param registry the STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS();
    }
}
