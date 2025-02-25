package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.MessageChat;

import java.util.List;

public interface MessageChatRepository extends JpaRepository<MessageChat, Integer> {
  List<MessageChat> findByChatChatId(Integer chatId);

}
