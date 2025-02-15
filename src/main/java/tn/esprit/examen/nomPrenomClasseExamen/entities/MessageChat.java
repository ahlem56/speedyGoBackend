package tn.esprit.examen.nomPrenomClasseExamen.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageChatId;
    private Date messageChatDateCreation;
    private String messageChatContent;
    private Boolean messageChatStatus;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
}

