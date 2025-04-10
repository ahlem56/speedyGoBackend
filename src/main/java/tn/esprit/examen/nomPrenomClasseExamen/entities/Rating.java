package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingId;

    @ManyToOne
    @JoinColumn(name = "rater_id") // The user who is giving the rating
    private User rater;

    @ManyToOne
    @JoinColumn(name = "rated_id") // The user who is being rated
    private User rated;

    @ManyToOne
    @JoinColumn(name = "trip_id") // The trip associated with this rating
    private Trip trip;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer score; // Rating score (1-5 stars)

    private String comment; // Optional comment

    @Enumerated(EnumType.STRING)
    private RatingType ratingType; // DRIVER_RATING or PASSENGER_RATING
}

