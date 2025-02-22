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

    private TripRepository tripRepository;
    private SimpleUserRepository simpleUserRepository;
    private DriverRepository driverRepository;

    @Override
    public Trip createTrip(Trip trip, Integer simpleUserId, Integer driverId) {
        SimpleUser simpleUser = simpleUserRepository.findById(simpleUserId)
                .orElseThrow(() -> new RuntimeException("SimpleUser not found"));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        trip.setSimpleUser(simpleUser);  // Assigning the SimpleUser
        trip.setDriver(driver);          // Assigning the Driver

        // Optionally set up payment if required
        // trip.setPayment(somePaymentObject);

        return tripRepository.save(trip);
    }


    @Override
    public Trip updateTrip(Integer tripId, Trip updatedTrip) {
        Trip existingTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));


        return tripRepository.save(existingTrip);
    }

    @Override
    public void deleteTrip(Integer tripId) {
        tripRepository.deleteById(tripId);
    }
    @Override
    public Trip getTripById(Integer tripId) {
        return tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));
    }
}