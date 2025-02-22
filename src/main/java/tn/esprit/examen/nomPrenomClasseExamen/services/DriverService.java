package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.DriverRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class DriverService implements IDriverService {
  @Autowired
  private DriverRepository driverRepository;
  @Autowired
  private PasswordEncoder passwordEncoder; // Injection du BCryptPasswordEncoder
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
    driverRepository.deleteById(driverId);

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
    return driverRepository.findByavailibilityDTrue();
  }



}
