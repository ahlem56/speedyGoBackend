package tn.esprit.examen.nomPrenomClasseExamen.controllers;



import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Rules;
import tn.esprit.examen.nomPrenomClasseExamen.services.UserActivityService;

@AllArgsConstructor
@RequestMapping("/rules")
@RestController
public class RulesController {

    @Autowired
    private UserActivityService userActivityService;

    @PostMapping("/create")
    public Rules createRules(@RequestBody Rules rules) {
        return userActivityService.createRules(rules);
    }


    @PutMapping("/update")
    public Rules updateRules(@RequestBody Rules rules) {
        return userActivityService.updateRules(rules);
    }
    @GetMapping
    public Rules getRules() {
        return userActivityService.getRules();
    }
}
