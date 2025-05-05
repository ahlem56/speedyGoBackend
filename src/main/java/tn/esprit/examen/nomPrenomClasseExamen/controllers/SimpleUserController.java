package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.services.SimpleUserService;

import java.util.List;

@RestController
@RequestMapping("/simpleuser")
@RequiredArgsConstructor
@Slf4j
public class SimpleUserController {
   private final SimpleUserService simpleUserService;

   @GetMapping("/all")
   public ResponseEntity<List<SimpleUser>> getAllUsers() {
      log.info("Fetching all users");
      List<SimpleUser> users = simpleUserService.getAllUsers();
      log.info("Retrieved {} users", users.size());
      return new ResponseEntity<>(users, HttpStatus.OK);
   }

   @PostMapping("/assign-partner")
   public ResponseEntity<?> assignPartnerToUser(@RequestBody AssignPartnerRequest request) {
      log.info("Assigning partnerId: {} to userId: {}", request.getPartnerId(), request.getUserId());
      simpleUserService.assignPartner(request.getUserId(), request.getPartnerId());
      log.info("Partner assigned successfully");
      return new ResponseEntity<>(HttpStatus.OK);
   }
}

class AssignPartnerRequest {
   private Integer userId;
   private Integer partnerId;

   public Integer getUserId() {
      return userId;
   }

   public void setUserId(Integer userId) {
      this.userId = userId;
   }

   public Integer getPartnerId() {
      return partnerId;
   }

   public void setPartnerId(Integer partnerId) {
      this.partnerId = partnerId;
   }
}