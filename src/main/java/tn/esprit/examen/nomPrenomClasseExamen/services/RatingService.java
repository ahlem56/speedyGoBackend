package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@Service
@AllArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final SentimentAnalysisService sentimentAnalysisService;
    private final SimpleUserRepository simpleUserRepository;
    private final DriverRepository driverRepository;

    public Rating createRating(Rating rating, Integer tripId, Integer raterId, Integer ratedId) {
        SimpleUser rater = simpleUserRepository.findById(raterId)
                .orElseThrow(() -> new RuntimeException("Rater user not found"));
        Driver rated = driverRepository.findById(ratedId)
                .orElseThrow(() -> new RuntimeException("Rated user not found"));
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Validate trip is completed
        if (trip.getReservationStatus() != ReservationStatus.COMPLETED) {
            throw new RuntimeException("Cannot rate for a trip that is not completed");
        }

        // Validate participants
        if (!trip.getSimpleUser().equals(rater) && !trip.getDriver().equals(rater)) {
            throw new RuntimeException("Only trip participants can rate");
        }

        if (!trip.getSimpleUser().equals(rated) && !trip.getDriver().equals(rated)) {
            throw new RuntimeException("Can only rate trip participants");
        }

        // Prevent duplicate ratings
        if (ratingRepository.existsByTripAndRaterAndRated(trip, rater, rated)) {
            throw new RuntimeException("You have already rated this user for this trip");
        }

        // Analyze the sentiment of the review comment
        if (rating.getComment() != null && !rating.getComment().isEmpty()) {
            String sentiment = sentimentAnalysisService.analyzeSentiment(rating.getComment());
            System.out.println("Sentiment: " + sentiment);  // You can store this sentiment for later use
            // Optionally, you could also update the driver's performance rating based on sentiment
        }

        // Set rating type automatically
        if (rated instanceof Driver) {
            rating.setRatingType(RatingType.DRIVER_RATING);
            trip.setReadyForPassengerRating(false); // Passenger has rated, disable further rating
        }

        rating.setRater(rater);
        rating.setRated(rated);
        rating.setTrip(trip);
        trip.setRated(true);  // Mark the trip as rated

        tripRepository.save(trip); // Save the updated trip status

        return ratingRepository.save(rating);

    }

    public Map<String, List<Rating>> getRatingsForUser(Integer userId) {
        Driver user = driverRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Fetch ratings the user has received
        List<Rating> ratingsReceived = ratingRepository.findByRated(user);
        // Fetch ratings the user has given
        System.out.println("Ratings Received: " + ratingsReceived.size());

        // You could return both or split them into separate methods if you want more control over what data is returned
        Map<String, List<Rating>> response = new HashMap<>();
        response.put("ratingsReceived", ratingsReceived);
        return response;

    }


    public Double getAverageRatingForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Rating> ratings = ratingRepository.findByRated(user);

        OptionalDouble average = ratings.stream()
                .mapToInt(Rating::getScore)
                .average();

        return average.isPresent() ? average.getAsDouble() : 0.0;
    }

    public List<Rating> getRatingsForTrip(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return ratingRepository.findByTrip(trip);
    }

    public boolean canRate(Integer tripId, Integer raterId, Integer ratedId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        User rater = userRepository.findById(raterId)
                .orElseThrow(() -> new RuntimeException("Rater not found"));
        User rated = userRepository.findById(ratedId)
                .orElseThrow(() -> new RuntimeException("Rated not found"));

        // Check if trip is completed
        if (trip.getReservationStatus() != ReservationStatus.COMPLETED) {
            return false;
        }

        // Check if rater is part of the trip
        if (!trip.getSimpleUser().equals(rater) && !trip.getDriver().equals(rater)) {
            return false;
        }

        // Check if rated is part of the trip
        if (!trip.getSimpleUser().equals(rated) && !trip.getDriver().equals(rated)) {
            return false;
        }

        // Check if rating already exists
        return !ratingRepository.existsByTripAndRaterAndRated(trip, rater, rated);
    }

    public List<User> getTopRatedDrivers(int limit) {
        return ratingRepository.findTopRatedDrivers(limit);
    }

    public List<User> getTopRatedPassengers(int limit) {
        return ratingRepository.findTopRatedPassengers(limit);
    }
}