package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Rating;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByRated(User ratedUser);
    List<Rating> findByRater(User raterUser);
    List<Rating> findByTrip(Trip trip);
    boolean existsByTripAndRaterAndRated(Trip trip, User rater, User rated);

    @Query("SELECT r.rated FROM Rating r " +
            "WHERE TYPE(r.rated) = Driver " +
            "GROUP BY r.rated " +
            "ORDER BY AVG(r.score) DESC")
    List<Driver> findTopRatedDrivers(Pageable pageable);

    @Query("SELECT r.rated FROM Rating r " +
            "WHERE TYPE(r.rated) = SimpleUser " +
            "GROUP BY r.rated " +
            "ORDER BY AVG(r.score) DESC")
    List<User> findTopRatedPassengers(Pageable pageable);

    default List<Driver> findTopRatedDrivers(int limit) {
        return findTopRatedDrivers(PageRequest.of(0, limit));
    }

    default List<User> findTopRatedPassengers(int limit) {
        return findTopRatedPassengers(PageRequest.of(0, limit));
    }
    @Transactional
    void deleteByTrip(Trip trip);  // <-- ADD THIS

}