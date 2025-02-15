package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Rules;
import tn.esprit.examen.nomPrenomClasseExamen.services.RulesService;

@AllArgsConstructor
@RequestMapping("/rules")
@RestController
public class RulesController {

    @Autowired
    private RulesService rulesService;

    @PostMapping("/createRule/{tripId}")
    public Rules createRules(@RequestBody Rules rules, @PathVariable Integer tripId) {
        return rulesService.createRule(rules,tripId);
    }

    @PutMapping("/updateRule/{ruleId}")
    public Rules updateRule(@PathVariable Integer ruleId, @RequestBody Rules rules) {
        return rulesService.updateRule(ruleId, rules);
    }

    @DeleteMapping("/deleteRule/{ruleId}")
    public void deleteRule(@PathVariable Integer ruleId) {
        rulesService.deleteRule(ruleId);
    }

    @GetMapping("/getRule/{ruleId}")
    public Rules getRuleById(@PathVariable Integer ruleId) {
        return rulesService.getRuleById(ruleId);
    }
}
