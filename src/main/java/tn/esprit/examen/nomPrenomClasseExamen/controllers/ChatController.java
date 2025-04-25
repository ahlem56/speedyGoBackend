package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Chat;
import tn.esprit.examen.nomPrenomClasseExamen.entities.MessageChat;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ChatRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.MessageChatRepository;

import java.util.Date;
import java.util.Optional;

@AllArgsConstructor
@RequestMapping("/chat")
@RestController
public class ChatController {
  private static final Logger log = LoggerFactory.getLogger(ChatController.class);
  //@MessageMapping("/chat-sendMessage")
//@SendTo("/topic/public")
//     public MessageChat sendMessage(@Payload MessageChat messageChat){
//return  messageChat;
//  }
//  //add username in websocket session
//  @MessageMapping("/chat-addUser")
//  @SendTo("/topic/public")
//  public MessageChat addUser(@Payload MessageChat messageChat, SimpMessageHeaderAccessor headerAccessor){
//   headerAccessor.getSessionAttributes().put("username",messageChat.getSender());
//return messageChat;
//}

  private SimpMessagingTemplate messagingTemplate;
  private  MessageChatRepository messageChatRepository;
  private  ChatRepository chatRepository;
  @MessageMapping("/send")
  @SendTo("/topic/messages")
  public MessageChat sendMessage(@Payload MessageChat message) {
    log.info("Message reçu : {}", message);

    if (message.getChat() == null || message.getChat().getChatId() == null) {
      log.error("Chat ID manquant dans le message !");
      return null;
    }

    Optional<Chat> chatOptional = chatRepository.findById(message.getChat().getChatId());
    if (chatOptional.isPresent()) {
      Chat chat = chatOptional.get();
      message.setMessageChatDateCreation(new Date());
      message.setMessageChatStatus(false);
      message.setChat(chat);

      MessageChat savedMessage = messageChatRepository.save(message);
      log.info("Message enregistré avec succès : {}", savedMessage);

      messagingTemplate.convertAndSend("/topic/messages", savedMessage);
      return savedMessage;
    } else {
      log.error("Chat non trouvé pour ID : {}", message.getChat().getChatId());
      return null;
    }
  }

  @GetMapping("/{chatId}")
  public Iterable<MessageChat> getChatHistory(@PathVariable Integer chatId) {
    return messageChatRepository.findByChatChatId(chatId);
  }

//  @MessageMapping("/send")
//  @SendTo("/topic/messages")
//  public MessageChat sendMessage(MessageChat message) {
//    Optional<Chat> chat = chatRepository.findById(message.getChat().getChatId());
//
//    if (chat.isPresent()) {
//      MessageChat msg = new MessageChat();
//      msg.setMessageChatContent(message.getMessageChatContent());
//      msg.setMessageChatDateCreation(new Date());
//      msg.setMessageChatStatus(false);
//      msg.setChat(chat.get());
//
//      messageChatRepository.save(msg);
//    }
//
//    return message;
//  }
//
//  @GetMapping("/{chatId}")
//  public Iterable<MessageChat> getChatHistory(@PathVariable Integer chatId) {
//    return messageChatRepository.findByChatChatId(chatId);
//  }


}
