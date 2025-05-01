package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.TripLocationDTO;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ParcelRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.IAdminService;
import tn.esprit.examen.nomPrenomClasseExamen.services.SimpleUserService;
import tn.esprit.examen.nomPrenomClasseExamen.services.SubscriptionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequestMapping("/Admin")
@RestController
public class AdminController {

  private IAdminService adminService;
  private UserRepository userRepository;
  private ParcelRepository parcelRepository;
  private SubscriptionService subscriptionService;
  private SimpleUserService simpleUserService;

  @PostMapping("/createAdmin")
  public Admin createAdmin(@RequestBody Admin admin) {
    return adminService.createAdmin(admin);
  }

  @GetMapping("/total-users")
  public ResponseEntity<Long> getTotalUsers() {
    Long totalUsers = userRepository.countTotalUsers();
    return ResponseEntity.ok(totalUsers);
  }

  @GetMapping("/total-trips")
  public ResponseEntity<Long> getTotalTrips() {
    long totalTrips = adminService.getTotalTrips();
    return ResponseEntity.ok(totalTrips);
  }

  @GetMapping("/trips-by-location")
  public List<TripLocationDTO> getTripsByLocation() {
    return adminService.getTripsByLocation();
  }

  @GetMapping("/revenue/monthly")
  public ResponseEntity<Map<String, BigDecimal>> getMonthlyRevenue() {
    Map<String, BigDecimal> monthlyRevenue = adminService.getMonthlyRevenue();
    return ResponseEntity.ok(monthlyRevenue);
  }

  @GetMapping("/total-parcels")
  public ResponseEntity<Long> getTotalParcels() {
    long totalParcels = parcelRepository.count();
    return ResponseEntity.ok(totalParcels);
  }
  // Endpoint to get the number of users subscribed to each subscription
  @GetMapping("/subscriptionStats")
  public ResponseEntity<Map<String, Integer>> getSubscriptionStatistics() {
    return ResponseEntity.ok(subscriptionService.getSubscriptionStatistics());
  }

  // Endpoint to get the number of carpool offers posted by users
  @GetMapping("/carpoolOfferStats")
  public ResponseEntity<Map<Integer, Integer>> getCarpoolOfferStatistics() {
    return ResponseEntity.ok(simpleUserService.getCarpoolOfferStatistics());
  }
}
