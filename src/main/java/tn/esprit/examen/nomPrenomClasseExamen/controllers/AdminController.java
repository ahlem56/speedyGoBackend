package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.services.IAdminService;

@AllArgsConstructor
@RequestMapping("/Admin")
@RestController
public class AdminController {

  private IAdminService adminService;
  @PostMapping("/createAdmin")
  public Admin createAdmin(@RequestBody Admin admin) {
    return adminService.createAdmin(admin);
  }
}
