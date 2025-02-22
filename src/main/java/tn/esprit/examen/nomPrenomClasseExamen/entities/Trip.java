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
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus = ReservationStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "driver_user_id")
    private Driver driver;


    @OneToMany(mappedBy = "trip", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Rules> ruleses = new LinkedHashSet<>();

    @OneToOne
    @JoinColumn(name = "payment_id")  // Association avec Payment
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "simple_user_id")
    private SimpleUser simpleUser;  // The association with SimpleUser
}