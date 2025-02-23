package tn.esprit.examen.nomPrenomClasseExamen.WebSockets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tn.esprit.examen.nomPrenomClasseExamen.entities.MessageChat;
import tn.esprit.examen.nomPrenomClasseExamen.entities.MessageType;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
//  private final SimpMessageSendingOperations messageTemplate;
//  @EventListener
//  public  void  handleWebSocketDisconnectListener(SessionDisconnectEvent event){
//    StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
//    String username = (String) headerAccessor.getSessionAttributes().get("username");
//    if(username!=null){
//      log.info("User disconnected : {}", username);
//      var messageChat = MessageChat.builder()
//        .messageType(MessageType.LEAVE)
//        .sender(username)
//        .build();
//      messageTemplate.convertAndSend("/topic/public",messageChat);
//
//    }
//  }
}
