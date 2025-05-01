package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Commission;
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;

import tn.esprit.examen.nomPrenomClasseExamen.repositories.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.CommissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import tn.esprit.examen.nomPrenomClasseExamen.services.UserService;

import java.security.Principal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@RequiredArgsConstructor
@RestController
@RequestMapping("/commissions")
public class CommissionController {
    private final  CommissionService commissionService;
    private final UserService userService;
    private final UserRepository userRepository;


    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<Commission>> getCommissionsByPartner(
            @PathVariable Long partnerId
    ) {
        System.out.println("🔥 CALLED COMMISSION ENDPOINT!");
        return ResponseEntity.ok(commissionService.getCommissionsForPartner(partnerId));
    }




    @GetMapping("/partner/{partnerId}/summary")
    public ResponseEntity<Map<String, BigDecimal>> getCommissionSummary(
            @PathVariable Long partnerId
    ) {
        return ResponseEntity.ok(
                commissionService.getCommissionSummary(partnerId)
        );
    }
}
