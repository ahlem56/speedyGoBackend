package tn.esprit.examen.nomPrenomClasseExamen.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.DriverRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TripRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@Service
public class TripService implements ITripService{

    private TripRepository tripRepository;
    private SimpleUserRepository simpleUserRepository;
    private DriverRepository driverRepository;
    private NotificationService notificationService;

    @Override
    public Trip createTrip(Trip trip, Integer simpleUserId, Integer driverId) {
        SimpleUser simpleUser = simpleUserRepository.findById(simpleUserId)
                .orElseThrow(() -> new RuntimeException("SimpleUser not found"));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Check if trip date is in the past
        if (trip.getTripDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Trip date must be in the future.");
        }

        // Check number of passengers
        if (trip.getNumberOfPassengers() < 1 || trip.getNumberOfPassengers() > 4) {
            throw new IllegalArgumentException("The number of passengers must be between 1 and 4.");
        }


        trip.setSimpleUser(simpleUser);
        trip.setDriver(driver);

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

    public List<Trip> getTripsForUser(Integer userId) {
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return tripRepository.findBySimpleUser(user);
    }


    public List<Trip> getAllTrips() {
        return tripRepository.findAll();  // This should fetch all trips from the DB
    }

    // New method to get trips for a specific driver
    public List<Trip> getTripsForDriver(Integer userId) {
        log.info("Fetching trips for driver with userId: {}", userId);  // Log the userId
        List<Trip> trips = tripRepository.findByDriver_UserId(userId);  // Use userId in the query
        log.info("Trips fetched: {}", trips);  // Log the trips list
        return trips;
    }

    public Trip acceptTrip(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found!"));
        trip.setReservationStatus(ReservationStatus.CONFIRMED);
        return tripRepository.save(trip);
    }

    public Trip refuseTrip(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found!"));
        trip.setReservationStatus(ReservationStatus.CANCELED);
        return tripRepository.save(trip);
    }


    public List<Trip> findTripsBySimpleUser(SimpleUser user) {
        return tripRepository.findTripsBySimpleUser(user);
    }

    // in TripService.java
    public List<Trip> getTripsByVehicle(Integer vehicleId) {
        return tripRepository.findByDriverVehicleVehiculeId(vehicleId);
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // Run every 1 minute
    public void sendTripReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fifteenMinutesLater = now.plusMinutes(15);

        List<Trip> upcomingTrips = tripRepository.findByTripDateBetweenAndReminderSentFalse(now, fifteenMinutesLater);

        for (Trip trip : upcomingTrips) {
            SimpleUser passenger = trip.getSimpleUser();

            // ✅ Build structured reminder notification
            Map<String, Object> message = new HashMap<>();
            message.put("type", "TRIP_REMINDER");
            message.put("message", String.format(
                    "Reminder: Your trip from %s to %s starts at %s 🚗💨",
                    trip.getTripDeparture(),
                    trip.getTripDestination(),
                    trip.getTripDate().toString()
            ));
            message.put("details", Map.of(
                    "departure", trip.getTripDeparture(),
                    "destination", trip.getTripDestination(),
                    "date", trip.getTripDate().toString()
            ));
            message.put("timestamp", new java.util.Date());

            try {
                String jsonMessage = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(message);

                // Send notification
                notificationService.createNotification(jsonMessage, passenger); // Save to DB
                notificationService.sendNotificationToUser(jsonMessage, passenger);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ✅ Mark as reminder sent
            trip.setReminderSent(true);
            tripRepository.save(trip);
        }
    }


}