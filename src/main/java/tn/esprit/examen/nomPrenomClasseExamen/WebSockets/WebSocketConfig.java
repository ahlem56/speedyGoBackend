package tn.esprit.examen.nomPrenomClasseExamen.WebSockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Register WebSocket endpoint with /examen prefix
    registry.addEndpoint("/ws-chat")  // Ensure the path is exactly /examen/ws-chat
            .setAllowedOriginPatterns("*")  // Allow connections from any origin (from Angular frontend)
            .withSockJS();  // SockJS fallback for unsupported browsers
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");  // Enable broker for subscribing to topics
    registry.setApplicationDestinationPrefixes("/app");  // Prefix for messages sent from the client
  }
}

