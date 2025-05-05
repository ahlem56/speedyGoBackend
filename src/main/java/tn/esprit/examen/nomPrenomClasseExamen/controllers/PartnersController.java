package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Partners;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Promotions;
import tn.esprit.examen.nomPrenomClasseExamen.exceptions.ErrorResponse;
import tn.esprit.examen.nomPrenomClasseExamen.services.PartnersService;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RequestMapping("/partners")
@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PartnersController {

    private final PartnersService partnersService;

    @PostMapping("/create")
    public ResponseEntity<?> createPartner(@RequestBody Partners partner) {
        log.info("Creating partner: {}", partner);
        try {
            Partners createdPartner = partnersService.createPartner(partner);
            return ResponseEntity.ok(createdPartner);
        } catch (Exception e) {
            log.error("Error creating partner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Partners>> getAllPartners() {
        log.info("Fetching all partners");
        List<Partners> partners = partnersService.getAllPartners();
        log.info("Retrieved {} partners", partners.size());
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPartnerById(@PathVariable Integer id) {
        log.info("Fetching partner with ID: {}", id);
        try {
            Partners partner = partnersService.getPartnerById(id);
            return ResponseEntity.ok(partner);
        } catch (RuntimeException e) {
            log.error("Error fetching partner with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePartner(@PathVariable Integer id, @RequestBody Partners partner) {
        log.info("Updating partner ID: {} with data: {}", id, partner);
        try {
            partner.setPartnerId(id);
            Partners updatedPartner = partnersService.updatePartner(partner);
            return ResponseEntity.ok(updatedPartner);
        } catch (RuntimeException e) {
            log.error("Error updating partner ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating partner ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePartner(@PathVariable Integer id) {
        log.info("Deleting partner with ID: {}", id);
        try {
            partnersService.deletePartner(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error deleting partner with ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error deleting partner ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/{partnerId}/assign-promotion/{promotionId}")
    public ResponseEntity<?> assignPromotionToPartner(
            @PathVariable Integer partnerId,
            @PathVariable Integer promotionId,
            @RequestBody(required = false) Promotions promotionDetails) {
        log.info("Assigning promotion ID: {} to partner ID: {}", promotionId, partnerId);
        try {
            Partners updatedPartner = partnersService.assignOrCreatePromotionToPartner(partnerId, promotionId, promotionDetails);
            return ResponseEntity.ok(updatedPartner);
        } catch (RuntimeException e) {
            log.error("Error assigning promotion to partner ID: {}: {}", partnerId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error assigning promotion to partner ID: {}: {}", partnerId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<?> getMonthlyRevenue() {
        log.info("Fetching monthly payment revenue");
        try {
            Map<String, Double> monthlyRevenue = partnersService.getMonthlyPaymentRevenue();
            return ResponseEntity.ok(monthlyRevenue);
        } catch (Exception e) {
            log.error("Error fetching monthly revenue: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to fetch monthly revenue: " + e.getMessage()));
        }
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<?> getDailyRevenue(
            @RequestParam("month") int month,
            @RequestParam("year") int year) {
        log.info("Fetching daily payment revenue for month: {}, year: {}", month, year);
        try {
            Map<String, Double> dailyRevenue = partnersService.getDailyPaymentRevenue(month, year);
            return ResponseEntity.ok(dailyRevenue);
        } catch (Exception e) {
            log.error("Error fetching daily revenue: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to fetch daily revenue: " + e.getMessage()));
        }
    }
}