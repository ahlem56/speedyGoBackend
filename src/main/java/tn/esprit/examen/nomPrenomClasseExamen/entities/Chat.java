package tn.esprit.examen.nomPrenomClasseExamen.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chatId;

    @ManyToOne
    @JoinColumn(name = "simple_user_user_id")
    private SimpleUser simpleUser;

    @ManyToOne
    @JoinColumn(name = "driver_user_id")
    private Driver driver;

    @OneToMany(mappedBy = "chat")
    private Set<MessageChat> messageChats;

}