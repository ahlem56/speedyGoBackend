package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @NotNull
    private String tripDeparture;
    @NotNull
    private String tripDestination;
    @NotNull
    @Future(message = "Trip date must be in the future") // Ensure the trip is not scheduled in the past
    private LocalDateTime tripDate; // Change to LocalDateTime for hours & minutes

    private String tripDuration;
    private BigDecimal tripPrice;
    @NotNull
    private TripType tripType;
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus = ReservationStatus.PENDING;
    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;
    @Column(nullable = false)
    private Boolean readyForDriverRating = false;  // default value
    @Column(nullable = false)
    private Boolean readyForPassengerRating=false; // Indicates if passenger can rate driver

    @NotNull
    @Min(value = 1, message = "Number of passengers must be at least 1")
    @Max(value = 4, message = "Number of passengers cannot exceed 4")
    @Column(name = "number_of_passengers", nullable = false)
    private Integer numberOfPassengers = 1;

    @Column(name = "is_rated")
    private Boolean isRated = false; // Default value is false

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false; // Default is false



    @ManyToOne
    @JoinColumn(name = "driver_user_id")
    private Driver driver;


    @OneToMany(mappedBy = "trip", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Rules> ruleses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "trip", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"trip", "partner", "user", "parcel"})
    private Set<Payment> payments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simple_user_id")
    @JsonIgnoreProperties({"partners", "carpoolOffered", "carpoolJoined", "events", "subscription", "complaints", "parcels", "notifications", "chats", "trips"})
    private SimpleUser simpleUser;
}