package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ReservationStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.services.TripService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("/trip")
@RestController
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/createTrip/{simpleUserId}/{driverId}")
    public Trip createTrip(@RequestBody Trip trip, @PathVariable Integer simpleUserId, @PathVariable Integer driverId, @RequestHeader("Authorization") String authorization) {
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


}