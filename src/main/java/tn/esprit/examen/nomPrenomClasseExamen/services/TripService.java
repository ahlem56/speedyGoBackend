package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.DriverRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TripRepository;

@Slf4j
@AllArgsConstructor
@Service
public class TripService implements ITripService{

    @Autowired
    private TripRepository tripRepository;
    private SimpleUserRepository simpleUserRepository;
    private DriverRepository driverRepository;

    public Trip createTrip(Trip trip, Integer simpleUserId, Integer driverId) {
        SimpleUser simpleUser = simpleUserRepository.findById(simpleUserId)
                .orElseThrow(() -> new RuntimeException("SimpleUser not found"));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        trip.setSimpleUser(simpleUser);  // Affectation du SimpleUser
        trip.setDriver(driver);          // Affectation du Driver

        return tripRepository.save(trip);
    }


    public Trip updateTrip(Integer tripId, Trip updatedTrip) {
        Trip existingTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        existingTrip.setTripDeparture(updatedTrip.getTripDeparture());
        existingTrip.setTripDestination(updatedTrip.getTripDestination());
        existingTrip.setTripDate(updatedTrip.getTripDate());
        existingTrip.setTripDuration(updatedTrip.getTripDuration());
        existingTrip.setTripPrice(updatedTrip.getTripPrice());
        existingTrip.setTripType(updatedTrip.getTripType());
        existingTrip.setDriver(updatedTrip.getDriver());
        existingTrip.setReservationStatus(updatedTrip.getReservationStatus());
        existingTrip.setPayment(updatedTrip.getPayment());

        return tripRepository.save(existingTrip);
    }

    public void deleteTrip(Integer tripId) {
        tripRepository.deleteById(tripId);
    }

    public Trip getTripById(Integer tripId) {
        return tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
    }
}
