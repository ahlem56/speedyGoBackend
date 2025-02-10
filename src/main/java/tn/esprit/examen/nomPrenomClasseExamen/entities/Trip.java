package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
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

public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tripId;
    private String tripDeparture;
    private String tripDestination;
    private Date tripDate;
    private Integer tripDuration;
    private Float tripPrice;
    private TripType tripType;

    @ManyToOne
    @JoinColumn(name = "driver_user_id")
    private Driver driver;
    @OneToOne
    private Reservation reservation;

    @OneToMany(mappedBy = "trip", orphanRemoval = true)
    private Set<Rules> ruleses = new LinkedHashSet<>();

}
