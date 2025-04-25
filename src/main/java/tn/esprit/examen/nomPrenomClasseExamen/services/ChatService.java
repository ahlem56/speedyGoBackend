package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.MessageChat;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.MessageChatRepository;

@Slf4j
@AllArgsConstructor
@Service
public class ChatService implements IChatService {
  private MessageChatRepository messageChatRepository;

  public MessageChat saveMessage(MessageChat message) {
    return messageChatRepository.save(message);
  }
}
