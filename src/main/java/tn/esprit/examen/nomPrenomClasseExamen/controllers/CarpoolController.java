package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Carpool;
import tn.esprit.examen.nomPrenomClasseExamen.entities.CarpoolStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.services.CarpoolService;

import java.util.List;

@RestController
@RequestMapping("/carpools")
@AllArgsConstructor
public class CarpoolController {

    private final CarpoolService carpoolService;

    @PostMapping("/add/{offerId}")
    public Carpool addCarpool(@RequestBody Carpool carpool, @PathVariable Integer offerId) {
        return carpoolService.ajouterCarpoolEtAffecterUser(carpool, offerId);
    }

    @PostMapping("/join/{carpoolId}/{simpleUserId}")
    public Carpool joinCarpool(@PathVariable Integer carpoolId, @PathVariable Integer simpleUserId, @RequestBody Integer numberOfPlaces) {
        return carpoolService.joinCarpool(carpoolId, simpleUserId, numberOfPlaces);
    }

    /*@PutMapping("/update/{carpoolId}/{offerId}")
    public Carpool updateCarpool(@PathVariable Integer carpoolId, @PathVariable Integer offerId, @RequestBody Carpool updatedCarpool) {
        return carpoolService.updateCarpool(carpoolId, offerId, updatedCarpool);
    }*/

    @DeleteMapping("/leave/{carpoolId}/{userId}")
    public void leaveCarpool(@PathVariable Integer carpoolId, @PathVariable Integer userId) {
        carpoolService.leaveCarpool(carpoolId, userId);
    }

    @DeleteMapping("/delete/{carpoolId}/{offerId}")
    public void deleteCarpool(@PathVariable Integer carpoolId, @PathVariable Integer offerId) {
        carpoolService.deleteCarpool(carpoolId, offerId);
    }

    @GetMapping("/get")
    public List<Carpool> getAllCarpools() {
        return carpoolService.getAllCarpools();
    }
    @GetMapping("/future")
    public List<Carpool> getFutureCarpools(@RequestParam Integer userId) {
        return carpoolService.getFutureCarpools(userId);
    }


    @GetMapping("/get/{carpoolId}")
    public Carpool getCarpoolById(@PathVariable Integer carpoolId) {
        return carpoolService.getCarpoolById(carpoolId);
    }

    @GetMapping("/{carpoolId}/offreur")
    public ResponseEntity<SimpleUser> getCarpoolOfferer(@PathVariable Integer carpoolId) {
        SimpleUser offerer = carpoolService.getCarpoolBySimpleUserOffer(carpoolId);
        if (offerer != null) {
            return ResponseEntity.ok(offerer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Carpool>> getCarpoolsByUser(@PathVariable Integer userId) {
        List<Carpool> carpools = carpoolService.getCarpoolsByUser(userId);
        return ResponseEntity.ok(carpools);
    }

    @PutMapping("/update/{carpoolId}/{userId}")
    public ResponseEntity<?> updateCarpool(@PathVariable Integer carpoolId, @PathVariable Integer userId, @RequestBody Carpool updatedCarpool) {
        try {
            Carpool carpool = carpoolService.updateCarpool(carpoolId, userId, updatedCarpool);
            return ResponseEntity.ok(carpool);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{carpoolId}/users")
    public List<SimpleUser> getUsersWhoJoinedCarpool(@PathVariable Integer carpoolId) {
        return carpoolService.getUsersWhoJoinedCarpool(carpoolId);
    }


    @GetMapping("/joined/{userId}")
    public ResponseEntity<List<Carpool>> getCarpoolsJoinedByUser(@PathVariable Integer userId) {
        List<Carpool> carpools = carpoolService.getCarpoolsJoinedByUser(userId);
        return ResponseEntity.ok(carpools);
    }


    @GetMapping("/recommended/{userId}")
    public List<Carpool> getRecommended(@PathVariable Integer userId) {
        return carpoolService.getOrderedRecommendedCarpools(userId);
    }

}

