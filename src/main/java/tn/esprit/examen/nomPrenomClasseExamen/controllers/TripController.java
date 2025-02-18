package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.services.TripService;

@AllArgsConstructor
@RequestMapping("/trip")
@RestController
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/createTrip/{simpleUserId}/{driverId}")
    public Trip createTrip(@RequestBody Trip trip, @PathVariable Integer simpleUserId, @PathVariable Integer driverId) {
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
}