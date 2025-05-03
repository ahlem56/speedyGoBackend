package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Rating;
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;
import tn.esprit.examen.nomPrenomClasseExamen.services.RatingService;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*") // Allow CORS from any origin (adjust later for security)
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

        Rating savedRating = ratingService.createRating(rating, tripId, raterId, ratedId);
        return ResponseEntity.ok(savedRating);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, List<Rating>>> getRatingsForUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(ratingService.getRatingsForUser(userId));
    }

    @GetMapping("/average/{userId}")
    public ResponseEntity<Float> getAverageRating(@PathVariable Integer userId) {
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

    // New endpoint to get the top-rated drivers
    @GetMapping("/top-rated-drivers")
    public ResponseEntity<List<Driver>> getTopRatedDrivers(
            @RequestParam(defaultValue = "5") int limit) {  // Default limit is 5 drivers
        List<Driver> topRatedDrivers = ratingService.getTopRatedDrivers(limit);
        return ResponseEntity.ok(topRatedDrivers);
    }
}
