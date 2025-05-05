package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;

import java.util.List;
import java.util.Optional;

public interface IDriverService {
  Driver createDriver(Driver driver);
  Driver updateDriver(Integer driverId, Driver driver);
  void deleteDriver(Integer driverId);
  Optional<Driver> getDriverById(Integer driverId);//Optional permet de gérer le cas où un Driver n'est pas trouvé dans la base de données.
  List<Driver> getAllDrivers();
  public List<Driver> getAvailableDrivers();
  public Driver updateDriverAvailability(Integer driverId, boolean availability);
  }
