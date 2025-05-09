package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LocationRecord;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Vehicle;
import tn.esprit.examen.nomPrenomClasseExamen.services.VehicleService;

import java.util.Date;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequestMapping("/vehicle")
@RestController
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/createVehicle")
    public Vehicle addVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }


    @PutMapping("/updateVehicle/{id}")
    public Vehicle updateVehicle(@PathVariable Integer id, @RequestBody Vehicle vehicle) {
        vehicle.setVehiculeId(id);
        return vehicleService.updateVehicle(vehicle);
    }

    @DeleteMapping("/deleteVehicle/{id}")
    public void deleteVehicle(@PathVariable Integer id) {
        vehicleService.deleteVehicle(id);
    }

    @GetMapping("/getVehicle/{id}")
    public Vehicle getVehicleById(@PathVariable Integer id) {
        return vehicleService.getVehicleById(id);
    }

    @GetMapping("/getAllVehicles")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @PostMapping("/assignVehicleToDriver/{vehicleId}/{driverId}")
    public void assignVehicleToDriver(@PathVariable Integer vehicleId, @PathVariable Integer driverId) {
        vehicleService.assignVehicleToDriver(vehicleId, driverId);
    }

    @GetMapping("/getAvailableVehicles")
    public List<Vehicle> getAvailableVehicles() {
        return vehicleService.getAvailableVehicles();
    }


    @PutMapping("/updateLocation/{vehicleId}")
    public Vehicle updateLocation(@PathVariable Integer vehicleId,@RequestParam Double latitude, @RequestParam Double longitude) {
        return vehicleService.updateLocation(vehicleId,latitude,longitude);
    }

    @GetMapping("/getTravelHistory/{vehicleId}")
    public List<LocationRecord> getTravelHistory(@PathVariable Integer vehicleId) {
        return vehicleService.getTravelHistory(vehicleId);
    }

    @GetMapping("/trip-coordinates/{tripId}")
    public Map<String, String> getTripCoordinates(@PathVariable Integer tripId) {
        return vehicleService.getTripDepartureAndDestination(tripId);
    }

    @GetMapping("/vehicles/expired-insurance")
    public List<Vehicle> getVehiclesWithExpiredInsurance() {
        List<Vehicle> allVehicles = vehicleService.getAllVehicles();
        Date today = new Date();

        return allVehicles.stream()
                .filter(vehicle -> Boolean.TRUE.equals(vehicle.getVehiculeInsuranceStatus())
                        && vehicle.getVehiculeInsuranceDate() != null
                        && vehicle.getVehiculeInsuranceDate().before(today))
                .toList();
    }

    @PostMapping("/saveCheckpoints/{vehicleId}")
    public Vehicle saveCheckpoints(
            @PathVariable Integer vehicleId,
            @RequestBody List<LocationRecord> checkpoints
    ) {
        return vehicleService.addCheckpoints(vehicleId, checkpoints);
    }

    @PutMapping("/markNextArrived/{vehicleId}")
    public Vehicle markNextArrived(@PathVariable Integer vehicleId) {
        return vehicleService.markNextCheckpointArrived(vehicleId);
    }

    @GetMapping("/checkpointsStatus/{vehicleId}")
    public List<Map<String, Object>> getCheckpointsStatus(@PathVariable Integer vehicleId) {
        return vehicleService.getCheckpointsStatus(vehicleId);
    }

}
