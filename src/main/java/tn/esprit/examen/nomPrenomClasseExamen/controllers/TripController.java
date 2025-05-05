package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ReservationStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TripRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.NotificationService;
import tn.esprit.examen.nomPrenomClasseExamen.services.TripService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("/trip")
@RestController
public class TripController {


    private TripService tripService;
    private TripRepository tripRepository;
    private NotificationService notificationService; // Inject NotificationService


    @PostMapping("/createTrip/{simpleUserId}/{driverId}")
    public Trip createTrip(@Valid @RequestBody Trip trip, @PathVariable Integer simpleUserId, @PathVariable Integer driverId, @RequestHeader("Authorization") String authorization) {
        if (trip.getReservationStatus() == null) {
            trip.setReservationStatus(ReservationStatus.PENDING);
        }
        return tripService.createTrip(trip, simpleUserId, driverId);
    }

    @PutMapping("/updateTrip/{tripId}")
    public Trip updateTrip(@PathVariable Integer tripId, @RequestBody Trip trip) {

        return tripService.updateTrip(tripId, trip);
    }

    @DeleteMapping("/deleteTrip/{tripId}")
    public void deleteTrip(@PathVariable Integer tripId) {
        tripService.deleteTrip(tripId);
    }

    @GetMapping("/getTrip/{tripId}")
    public Trip getTripById(@PathVariable Integer tripId) {
        return tripService.getTripById(tripId);
    }

    // Get all trips for a user
    @GetMapping("/getTripsForUser/{userId}")
    public List<Trip> getTripsForUser(@PathVariable Integer userId) {
        return tripService.getTripsForUser(userId);
    }

    @GetMapping("/getAllTrips")
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/getTripsForDriver/{driverId}")
    public List<Trip> getTripsForDriver(@PathVariable Integer driverId) {
        return tripService.getTripsForDriver(driverId);
    }

    @PutMapping("/acceptTrip/{tripId}")
    public ResponseEntity<Trip> acceptTrip(@PathVariable Integer tripId) {
        Trip updatedTrip = tripService.acceptTrip(tripId);

        // Trigger notification to SimpleUser when a driver accepts a trip
        SimpleUser simpleUser = updatedTrip.getSimpleUser();
        notificationService.sendTripAcceptanceNotification(updatedTrip);  // Send trip details to clients

        return ResponseEntity.ok(updatedTrip);
    }

    @PutMapping("/refuseTrip/{tripId}")
    public ResponseEntity<Trip> refuseTrip(@PathVariable Integer tripId) {
        Trip updatedTrip = tripService.refuseTrip(tripId);

        // Send a notification to the user (passenger) about the refusal
        notificationService.sendTripRefusalNotification(updatedTrip);

        return ResponseEntity.ok(updatedTrip);
    }




    @PutMapping("/completeTrip/{tripId}")
    public ResponseEntity<Trip> completeTrip(@PathVariable Integer tripId) {
        Trip trip = tripService.getTripById(tripId);

        if (trip.getReservationStatus() != ReservationStatus.CONFIRMED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        trip.setReservationStatus(ReservationStatus.COMPLETED);
        trip.setReadyForDriverRating(true); // Enable driver to rate passenger
        trip.setReadyForPassengerRating(true); // Enable passenger to rate driver

        tripRepository.save(trip);

        return ResponseEntity.ok(trip);
    }

    @GetMapping("/getTripsByVehicle/{vehicleId}")
    public List<Trip> getTripsByVehicle(@PathVariable Integer vehicleId) {
        return tripService.getTripsByVehicle(vehicleId);
    }

    }



