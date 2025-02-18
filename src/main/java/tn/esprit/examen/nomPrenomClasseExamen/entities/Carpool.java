package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
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
    private String carpoolDestination ;
    private Date carpoolDate ;
    private Integer carpoolCapacity ;
    private String carpoolCondition ;
    private Float carpoolPrice;



    @ManyToOne
    @JoinColumn(name = "simple_user_user_id")
    private SimpleUser simpleUser;

}
