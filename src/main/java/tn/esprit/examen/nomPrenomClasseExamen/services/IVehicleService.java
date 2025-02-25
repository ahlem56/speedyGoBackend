package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Vehicle;

import java.util.List;

public interface IVehicleService {

    Vehicle addVehicle(Vehicle vehicle);
    Vehicle updateVehicle(Vehicle vehicle);
    void deleteVehicle(Integer id);
    Vehicle getVehicleById(Integer id);
    List<Vehicle> getAllVehicles();
    void assignVehicleToDriver(Integer vehicleId, Integer driverId);

}
