package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    @NotNull(message = "La date de l'événement est obligatoire")
    @FutureOrPresent(message = "La date doit être présente ou future")
    private Date eventDate;

    @NotBlank(message = "La description ne peut pas être vide")
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String eventDescription;

    @NotBlank(message = "Le lieu ne peut pas être vide")
    @Size(max = 100, message = "Le lieu ne peut pas dépasser 100 caractères")
    private String eventLocation;

    @NotNull(message = "Le nombre maximum de participants est obligatoire")
    @Min(value = 1, message = "Il doit y avoir au moins 1 participant autorisé")
    private Integer maxParticipants;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String photo;

    @Transient//vous souhaitez calculer une valeur à partir d'autres
    // champs sans que cette valeur calculée soit stockée dans la base de données
    private boolean registered;

    @Transient
    private int currentParticipants;

    @ManyToMany
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<SimpleUser> simpleUsers ;

}
