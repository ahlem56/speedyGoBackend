package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)  // This will ignore any extra fields in JSON that don't exist in the class

public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingId;

    @ManyToOne
    @JoinColumn(name = "rater_id") // The user who is giving the rating
    private SimpleUser rater;  // Change to SimpleUser

    @ManyToOne
    @JoinColumn(name = "rated_id") // The user who is being rated
    private Driver rated;  // Change to Driver

    @ManyToOne
    @JoinColumn(name = "trip_id") // The trip associated with this rating
    private Trip trip;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer score; // Rating score (1-5 stars)

    @Column(nullable = true)
    private String comment; // Optional comment

    @Enumerated(EnumType.STRING)
    private RatingType ratingType; // DRIVER_RATING or PASSENGER_RATING

    // Add sentiment fields
    @Column(nullable = true)
    private String sentiment; // Store sentiment value (positive/negative)

    @Column(nullable = true)
    private Double sentimentScore; // Store sentiment score (0-1 scale or a different scale)
}

