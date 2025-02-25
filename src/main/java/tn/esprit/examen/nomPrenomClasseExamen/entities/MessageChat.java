package tn.esprit.examen.nomPrenomClasseExamen.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageChat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer messageChatId;
  private Date messageChatDateCreation;
  private  String sender;
  private String messageChatContent;
  private Boolean messageChatStatus;

  @ManyToOne
  @JoinColumn(name = "chat_id")
  private Chat chat;



}

