package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.LocationRecord;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Vehicle;
import tn.esprit.examen.nomPrenomClasseExamen.services.VehicleService;

import java.util.List;

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
        vehicle.setVehiculeId(id);  // Ensure the ID in the request matches the ID in the path
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
}
