package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Commission;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.CommissionRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.CommissionService;
import tn.esprit.examen.nomPrenomClasseExamen.services.SimpleUserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("simpleUser")
public class SimpleUserController {

   @Autowired
   private SimpleUserService simpleUserService;

   // GET All Simple Users
   @GetMapping
   public List<SimpleUser> getAllSimpleUsers() {
      return simpleUserService.getAllSimpleUsers();
   }

   // Optionally you can add more endpoints later
   // Example: Get simple user by ID
   @GetMapping("/{id}")
   public SimpleUser getSimpleUserById(@PathVariable Integer id) {
      return simpleUserService.getSimpleUserById(id);
   }

   // Example: Delete a SimpleUser by ID (Optional)
   @DeleteMapping("/{id}")
   public void deleteSimpleUser(@PathVariable Integer id) {
      simpleUserService.deleteSimpleUser(id);
   }
   }