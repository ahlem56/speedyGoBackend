package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.DriverRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.RatingRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TripRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class DriverService implements IDriverService {

  private DriverRepository driverRepository;
  private PasswordEncoder passwordEncoder; // Injection du BCryptPasswordEncoder
  private TripRepository tripRepository;
  private RatingRepository ratingRepository;
  @Override
  public Driver createDriver(Driver driver) {
    // Hachage du mot de passe avant de le sauvegarder
    String encryptedPassword = passwordEncoder.encode(driver.getUserPassword());
    driver.setUserPassword(encryptedPassword);
    return driverRepository.save(driver);  }

  @Override
  public Driver updateDriver(Integer driverId, Driver driver) {
    if (driverRepository.existsById(driverId)) {
      driver.setId(driverId);  // Set the ID to ensure the correct entity is updated
      return driverRepository.save(driver);
    }
    return null;  }
  @Override
  public void deleteDriver(Integer driverId) {
    Optional<Driver> driverOpt = driverRepository.findById(driverId);
    if (driverOpt.isPresent()) {
      Driver driver = driverOpt.get();

      // 1. Delete Ratings linked to Driver's Trips
      for (Trip trip : driver.getTrips()) {
        ratingRepository.deleteByTrip(trip);  // You must create a method in RatingRepository
      }

      // 2. Delete Trips
      tripRepository.deleteAll(driver.getTrips());

      // 3. Finally delete the Driver
      driverRepository.deleteById(driverId);
    }
  }

  @Override
  public Optional<Driver> getDriverById(Integer driverId) {
    return driverRepository.findById(driverId);
  }

  @Override
  public List<Driver> getAllDrivers() {
    return driverRepository.findAll();
  }

  @Override
  public List<Driver> getAvailableDrivers() {
    return driverRepository.findByavailabilityDTrue();
  }


  @Override
  public Driver updateDriverAvailability(Integer driverId, boolean availability) {
    Optional<Driver> driverOpt = driverRepository.findById(driverId);
    if (driverOpt.isPresent()) {
      Driver driver = driverOpt.get();
      driver.setAvailabilityD(availability);
      return driverRepository.save(driver);
    }
    return null;
  }



}
