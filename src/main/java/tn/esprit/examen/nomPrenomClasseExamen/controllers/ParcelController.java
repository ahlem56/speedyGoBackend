package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Status;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ParcelRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.IParcelService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequestMapping("/parcel")
@RestController
public class ParcelController {
  //projet hedha y5dim fih bil les roles
  @Autowired
  private IParcelService parcelService;
  @Autowired
  private ParcelRepository parcelRepository;
  // Create a new parcel and assign it to a driver and a user
  //http://localhost:8089/examen/parcel/createParcel/4/5
//  @PostMapping("/createParcel/{userId}/{driverId}")
//  public Parcel createParcelWithAssignment(
//    @RequestBody Parcel parcel,
//    @PathVariable("userId") Integer userId,
//    @PathVariable("driverId") Integer driverId) {
//    return parcelService.createParcelWithAssignment(parcel, driverId, userId);
//  }
  @PostMapping("/createParcel/{userId}")
  public Parcel createParcel(@Valid  @RequestBody Parcel parcel, @PathVariable Integer userId, @RequestHeader("Authorization") String authorization) {
    return parcelService.createParcel(parcel, userId);
  }

  // 2️⃣ Affectation d’un colis existant à un Driver (par l'admin)
  @PutMapping("/assign/{parcelId}/{driverId}")
  public Parcel assignParcelToDriver(@PathVariable Integer parcelId, @PathVariable Integer driverId) {
    return parcelService.assignParcelToDriver(parcelId, driverId);
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
  //Recover all the parcels by Driver Id
  @GetMapping("/driver/{driverId}")
  public List<Parcel> getParcelsByDriver(@PathVariable Long driverId) {
    return parcelService.getParcelsByDriver(driverId);
  }
  @GetMapping("/user/{userId}")
  public List<Parcel> getParcelsForUser(@PathVariable Integer userId) {
    return parcelService.getParcelsForUser(userId);
  }
  // Delete a parcel by its ID
  @DeleteMapping("/delete/{id}")
  public void deleteParcel(@PathVariable("id") Integer id) {
    parcelService.deleteParcel(id);
  }
  // Récupérer les colis créés après une certaine date
  @GetMapping("/after")
  public List<Parcel> getParcelsAfterDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
    return parcelService.getParcelsAfterDate(date);
  }

  // Récupérer les colis créés avant une certaine date
  @GetMapping("/before")
  public List<Parcel> getParcelsBeforeDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
    return parcelService.getParcelsBeforeDate(date);
  }
  @GetMapping("/calculate-price")
  public double calculateParcelPrice(@RequestParam double weight) {
    return parcelService.determineParcelPrice(weight);
  }
  @GetMapping("/statistics")
  public Map<String, Long> getParcelStatistics(@RequestParam String date) throws ParseException {
    // Convertir la chaîne en Date (format: yyyy-MM-dd)
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date parsedDate = sdf.parse(date);

    // Obtenez les statistiques pour la journée, la semaine et le mois
    long dailyCount = parcelService.getDeliveredParcelsByDay(parsedDate);
    long weeklyCount = parcelService.getDeliveredParcelsByWeek(parsedDate);
    long monthlyCount = parcelService.getDeliveredParcelsByMonth(parsedDate);

    // Retourner les statistiques sous forme de map
    return Map.of(
      "daily", dailyCount,
      "weekly", weeklyCount,
      "monthly", monthlyCount
    );
  }
  //API POUR changer l'etat de parcel en SHIPPED
  // Endpoint pour marquer un colis comme SHIPPED
  @PutMapping("/{id}/shipped")
  public ResponseEntity<Map<String, String>> markParcelAsShipped(@PathVariable("id") Long parcelId) {
    try {
      parcelService.markAsShipped(parcelId);
      Map<String, String> response = new HashMap<>();
      response.put("status", "success");
      response.put("message", "Parcel marked as shipped");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, String> response = new HashMap<>();
      response.put("status", "error");
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
  //API POUR changer l'etat de parcel en DELIVERED

  @PutMapping("/{id}/delivered")
  public ResponseEntity<Map<String, String>> markParcelAsDelivered(@PathVariable("id") Long parcelId) {
    try {
      parcelService.markAsDelivered(parcelId);
      Map<String, String> response = new HashMap<>();
      response.put("status", "success");
      response.put("message", "Parcel marked as delivered");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, String> response = new HashMap<>();
      response.put("status", "error");
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
  @PutMapping("/archive-delivered")
  public List<Parcel> archiveDeliveredParcels() {
    return parcelService.archiveDeliveredParcels();
  }
  // filte les parcels by Status
  @GetMapping("/byStatus")
  public List<Parcel> getParcelsByStatus(@RequestParam Status status) {
    return parcelService.getParcelsByStatus(status);  // Appel de la méthode service
  }
  // Méthode pour obtenir les revenus mensuels pour l'année actuelle
  @GetMapping("/yearly-revenue")
  public Map<String, Float> getYearlyRevenue() {
    return parcelService.calculateYearlyRevenue();
  }
}




