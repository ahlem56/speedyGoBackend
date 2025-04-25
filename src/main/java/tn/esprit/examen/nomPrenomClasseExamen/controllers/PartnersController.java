package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Partners;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Promotions;
import tn.esprit.examen.nomPrenomClasseExamen.services.PartnersService;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/partners")
@RestController
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
    public ResponseEntity<Void> deletePartner(@PathVariable Integer id) {
        partnersService.deletePartner(id);
        return ResponseEntity.ok().build();
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




