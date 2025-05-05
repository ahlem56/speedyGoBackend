package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Commission;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.CommissionRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.CommissionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
public class SimpleUserController {

   }