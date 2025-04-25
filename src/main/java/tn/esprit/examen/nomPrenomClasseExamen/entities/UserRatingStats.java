package tn.esprit.examen.nomPrenomClasseExamen.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class UserRatingStats {
    private Double averageRating;
    private int totalRatings;
    private Map<Integer, Long> ratingDistribution;
}
