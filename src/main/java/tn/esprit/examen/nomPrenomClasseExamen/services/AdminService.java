package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.entities.TripLocationDTO;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.AdminRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TripRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@Service
public class AdminService implements IAdminService {
    private AdminRepository adminRepository;


    private PasswordEncoder passwordEncoder; // Injection du BCryptPasswordEncoder
    private TripRepository tripRepository;

    public long getTotalTrips() {
        // Assuming you have a Trip entity and it stores the trips in the database
        return tripRepository.count();  // Simple count of trips
    }

    @Override
    public Admin createAdmin(Admin admin) {
        // Hachage du mot de passe avant de le sauvegarder
        String encryptedPassword = passwordEncoder.encode(admin.getUserPassword());
        admin.setUserPassword(encryptedPassword);
        return adminRepository.save(admin);
    }


    public List<TripLocationDTO> getTripsByLocation() {
        List<Trip> trips = tripRepository.findAll();

        // Group trips by their departure and destination locations (combine them)
        Map<String, Long> locationCountMap = trips.stream()
                .flatMap(trip -> Stream.of(trip.getTripDeparture(), trip.getTripDestination()))
                .collect(Collectors.groupingBy(location -> location, Collectors.counting()));

        // Filter locations visited at least 3 times
        locationCountMap = locationCountMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= 3) // Only keep locations visited at least 3 times
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Fetch latitude and longitude directly from the Trip entity
        List<TripLocationDTO> tripLocationDTOs = locationCountMap.entrySet().stream()
                .map(entry -> {
                    String location = entry.getKey();
                    Long count = entry.getValue();

                    // Find the first trip with this location (departure or destination)
                    Trip trip = trips.stream()
                            .filter(t -> t.getTripDeparture().equals(location) || t.getTripDestination().equals(location))
                            .findFirst()
                            .orElse(null);

                    // If the trip is found, use its latitude and longitude
                    Double latitude = (trip != null) ? trip.getLatitude().doubleValue() : 0.0;
                    Double longitude = (trip != null) ? trip.getLongitude().doubleValue() : 0.0;

                    return new TripLocationDTO(location, latitude, longitude, count.intValue());
                })
                .collect(Collectors.toList());

        return tripLocationDTOs;
    }


    public Map<String, BigDecimal> getMonthlyRevenue() {
        // Get the current date
        LocalDateTime endDate = LocalDateTime.now();

        // Get the start date as 12 months ago
        LocalDateTime startDate = endDate.minusMonths(12).toLocalDate().atStartOfDay();

        // Fetch trips within the last year
        List<Trip> trips = tripRepository.findTripsByDateRange(startDate, endDate);

        // Generate a list of all month names (in English) to ensure all months appear
        String[] months = {
                "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST",
                "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        };

        // Group trips by month and calculate total revenue for each month
        Map<String, BigDecimal> monthlyRevenue = trips.stream()
                .filter(trip -> trip.getTripPrice() != null && trip.getTripPrice().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.groupingBy(
                        trip -> trip.getTripDate().getMonth().toString().toUpperCase(), // Group by month name (uppercase)
                        Collectors.reducing(BigDecimal.ZERO, Trip::getTripPrice, BigDecimal::add) // Sum up revenue per month
                ));

        // Ensure all months are represented, adding 0 revenue for months with no trips
        Map<String, BigDecimal> completeMonthlyRevenue = new HashMap<>();
        for (String month : months) {
            completeMonthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, BigDecimal.ZERO));
        }

        return completeMonthlyRevenue;
    }




    private BigDecimal calculateRevenue(List<Trip> trips) {
        return trips.stream()
                .filter(trip -> trip.getTripPrice() != null && trip.getTripPrice().compareTo(BigDecimal.ZERO) > 0)
                .map(Trip::getTripPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Correct accumulator
    }



}
