package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
        // Fetch the rater, rated, and trip entities
        SimpleUser rater = simpleUserRepository.findById(raterId)
                .orElseThrow(() -> new RuntimeException("Rater user not found"));
        Driver rated = driverRepository.findById(ratedId)
                .orElseThrow(() -> new RuntimeException("Rated user not found"));
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Validate the trip and participants
        if (trip.getReservationStatus() != ReservationStatus.COMPLETED) {
            throw new RuntimeException("Cannot rate for a trip that is not completed");
        }

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

        // Analyze sentiment from the review comment
        if (rating.getComment() != null && !rating.getComment().isEmpty()) {
            SentimentResponse sentimentResponse = sentimentAnalysisService.analyzeSentiment(rating.getComment());
            String sentiment = sentimentResponse.getSentiment();
            Double sentimentScore = sentimentResponse.getPredictedScore();  // This score comes from the Flask API response

            // Store sentiment and sentiment score
            rating.setSentiment(sentiment);
            rating.setSentimentScore(sentimentScore);

            // **Directly use the score from Flask API response** for the rating score
            int finalScore = (int) Math.round(sentimentResponse.getPredictedScore());  // No recalculation, just use Flask's score
            finalScore = Math.max(1, Math.min(finalScore, 5));  // Ensure the score is between 1 and 5

            // Set the final score based on the value returned from the Flask API
            rating.setScore(finalScore);  // Set the score as returned from the Flask API
        }

        // Automatically set the rating type
        if (rated instanceof Driver) {
            rating.setRatingType(RatingType.DRIVER_RATING);
            trip.setReadyForPassengerRating(false); // Mark as rated by the passenger
        }

        rating.setRater(rater);
        rating.setRated(rated);
        rating.setTrip(trip);
        trip.setIsRated(true); // Mark the trip as rated

        tripRepository.save(trip);  // Save the updated trip status

        // Save the rating in the database
        return ratingRepository.save(rating);  // Save the rating in the database
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


    public Float getAverageRatingForUser(Integer userId) {
        Driver user = driverRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Rating> ratings = ratingRepository.findByRated(user);

        OptionalDouble average = ratings.stream()
                .mapToInt(Rating::getScore)
                .average();

        // Convert the double to Float
        return average.isPresent() ? (float) average.getAsDouble() : 0.0f;  // Explicitly cast to Float
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

    public List<Driver> getTopRatedDrivers(int limit) {
        // Fetch top-rated drivers
        List<Driver> drivers = ratingRepository.findTopRatedDrivers(PageRequest.of(0, limit));

        // For each driver, calculate the average rating
        for (Driver driver : drivers) {
            double averageRating = getAverageRatingForUser(driver.getUserId());
            // Convert the double average rating to Float
            driver.setPerformanceRatingD((float) averageRating);  // Cast double to float
        }
        return drivers;
    }


    public List<User> getTopRatedPassengers(int limit) {
        return ratingRepository.findTopRatedPassengers(limit);
    }
}