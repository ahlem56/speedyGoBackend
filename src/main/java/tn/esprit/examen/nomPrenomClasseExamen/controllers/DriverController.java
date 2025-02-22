package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.services.IDriverService;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RequestMapping("/driver")
@RestController
public class DriverController {
  @Autowired
  private IDriverService driverService;
  // http://localhost:8089/examen/driver/createDriver
  @PostMapping("/createDriver")
  public Driver createDriver(@RequestBody Driver driver) {
    return driverService.createDriver(driver);
  }

  @GetMapping("/get-all-drivers")
  public List<Driver> getAllDrivers() {
    return driverService.getAllDrivers();
  }

  @GetMapping("/find-driver/{id}")
  public Optional<Driver> getDriverById(@PathVariable("id") Integer id) {
    return driverService.getDriverById(id);
  }

  @PutMapping("/update/{id}")
  public Driver updateDriver(
    @PathVariable("id") Integer id,
    @RequestBody Driver driverDetails) {
    return driverService.updateDriver(id, driverDetails);
  }

  @DeleteMapping("/delete/{id}")
  public void deleteDriver(@PathVariable("id") Integer id) {
    driverService.deleteDriver(id);
  }


  @GetMapping("/get-available-drivers")
  public List<Driver> getAvailableDrivers() {
    return driverService.getAvailableDrivers();
  }

}
