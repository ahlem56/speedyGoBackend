package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Rating;
import tn.esprit.examen.nomPrenomClasseExamen.services.RatingService;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequestMapping("/ratings")
@RestController
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/rate/{tripId}/{raterId}/{ratedId}")
    public ResponseEntity<Rating> createRating(
            @RequestBody Rating rating,
            @PathVariable Integer tripId,
            @PathVariable Integer raterId,
            @PathVariable Integer ratedId) {
        return ResponseEntity.ok(
                ratingService.createRating(rating, tripId, raterId, ratedId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, List<Rating>>> getRatingsForUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(ratingService.getRatingsForUser(userId));
    }

    @GetMapping("/average/{userId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable Integer userId) {
        return ResponseEntity.ok(ratingService.getAverageRatingForUser(userId));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<Rating>> getRatingsForTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(ratingService.getRatingsForTrip(tripId));
    }

    @GetMapping("/can-rate/{tripId}/{raterId}/{ratedId}")
    public ResponseEntity<Boolean> canRate(
            @PathVariable Integer tripId,
            @PathVariable Integer raterId,
            @PathVariable Integer ratedId) {
        return ResponseEntity.ok(
                ratingService.canRate(tripId, raterId, ratedId)
        );
    }
}