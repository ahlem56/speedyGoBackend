package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LocationRecord;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Vehicle;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.DriverRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TripRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.VehicleRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class VehicleService implements IVehicleService {
    private VehicleRepository vehicleRepository;
    private DriverRepository driverRepository;
    private TripRepository tripRepository ;

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle updateVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public void deleteVehicle(Integer id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public Vehicle getVehicleById(Integer id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public void assignVehicleToDriver(Integer vehicleId, Integer driverId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Conducteur introuvable"));

        driver.setVehicle(vehicle);
        driverRepository.save(driver);
    }

    /*@Override
    public Vehicle updateLocation(Integer vehicleId, Double latitude, Double longitude) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).get();
        vehicle.setLatitude(latitude);
        vehicle.setLongitude(longitude);
        return vehicleRepository.save(vehicle);
    }

     */
    @Override
    public Vehicle updateLocation(Integer vehicleId, Double latitude, Double longitude) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        vehicle.setLatitude(latitude);
        vehicle.setLongitude(longitude);
        // Mise à jour de l'instantané de la position et de l'heure
        Date now = new Date();
        vehicle.setUpdateTime(now);

        // Ajout d'un nouvel enregistrement dans l'historique de trajet
        vehicle.getTravelHistory().add(new LocationRecord(latitude, longitude, now));

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByDriverIsNull();
    }

    // Méthode pour récupérer l'historique depuis l'entité Vehicle
    public List<LocationRecord> getTravelHistory(Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        return vehicle.getTravelHistory();
    }


    public Map<String, String> getTripDepartureAndDestination(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip introuvable"));

        Map<String, String> tripInfo = new HashMap<>();
        tripInfo.put("departure", trip.getTripDeparture());
        tripInfo.put("destination", trip.getTripDestination());

        return tripInfo;
    }


}
