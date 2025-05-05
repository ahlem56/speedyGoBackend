package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Partners;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Promotions;
import tn.esprit.examen.nomPrenomClasseExamen.services.PartnersService;

import java.util.HashMap;
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
    public ResponseEntity<Partners> createPartner(@RequestBody Partners partner) {
        return ResponseEntity.ok(partnersService.createPartner(partner));
    }

    @GetMapping
    public ResponseEntity<List<Partners>> getAllPartners() {
        return ResponseEntity.ok(partnersService.getAllPartners());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partners> getPartnerById(@PathVariable Integer id) {
        return ResponseEntity.ok(partnersService.getPartnerById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePartner(@PathVariable Integer id) {
        try {
            partnersService.deletePartner(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error deleting partner with id {}: {}", id, e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("Unexpected error deleting partner with id {}: {}", id, e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{partnerId}/assign-promotion/{promotionId}")
    public ResponseEntity<Partners> assignPromotionToPartner(
            @PathVariable Integer partnerId,
            @PathVariable Integer promotionId,
            @RequestBody(required = false) Promotions promotionDetails) {

        Partners updatedPartner = partnersService.assignOrCreatePromotionToPartner(partnerId, promotionId, promotionDetails);

        return ResponseEntity.ok(updatedPartner);
    }
}




