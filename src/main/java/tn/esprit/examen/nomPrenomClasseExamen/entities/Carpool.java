package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carpool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer carpoolId;
    private String carpoolDeparture ;
    @NonNull
    private String carpoolDestination ;
    @NonNull
    private LocalDate carpoolDate ;
    @NonNull
    private LocalTime carpoolTime ;
    @NonNull
    private Integer carpoolCapacity ;
    private String carpoolCondition ;
    private Float carpoolPrice;
    @Enumerated(EnumType.STRING)
    private CarpoolStatus carpoolStatus=CarpoolStatus.available ;
    private String licensePlate ;


    private LocalDateTime creationTime = LocalDateTime.now();


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "simple_user_user_id")
    private SimpleUser simpleUserOffer;

    @ManyToMany
    @JoinTable(
            name = "carpool-join",
            joinColumns = @JoinColumn(name = "carpool_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<SimpleUser> simpleUserJoin = new LinkedHashSet<>();

    @Column(columnDefinition = "TEXT")
    private String joinedUsersPlaces; // JSON string: {"userId1": places, "userId2": places}


}
