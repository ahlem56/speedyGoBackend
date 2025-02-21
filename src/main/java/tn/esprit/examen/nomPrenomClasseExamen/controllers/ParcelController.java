package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.services.IParcelService;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/parcel")
@RestController
public class ParcelController {
  //projet hedha y5dim fih bil les roles 
  @Autowired
  private IParcelService parcelService;
  // Create a new parcel and assign it to a driver and a user
  //http://localhost:8089/examen/parcel/createParcel/4/5
  @PostMapping("/createParcel/{userId}/{driverId}")
  public Parcel createParcelWithAssignment(
    @RequestBody Parcel parcel,
    @PathVariable("userId") Integer userId,
    @PathVariable("driverId") Integer driverId) {
    return parcelService.createParcelWithAssignment(parcel, driverId, userId);
  }

  // Get a list of all parcels
  @GetMapping("/get-all-parcels")
  public List<Parcel> getAllParcels() {
    return parcelService.getAllParcels();
  }

  // Get a single parcel by its ID
  @GetMapping("/find-parcel/{id}")
  public Parcel getParcelById(@PathVariable("id") Integer id) {
    return parcelService.getParcelById(id);
  }

  // Update an existing parcel by its ID
  @PutMapping("/update/{id}")
  public Parcel updateParcel(
    @PathVariable("id") Integer id,
    @RequestBody Parcel parcelDetails) {
    return parcelService.updateParcel(id, parcelDetails);
  }

  // Delete a parcel by its ID
  @DeleteMapping("/delete/{id}")
  public void deleteParcel(@PathVariable("id") Integer id) {
    parcelService.deleteParcel(id);
  }
}
