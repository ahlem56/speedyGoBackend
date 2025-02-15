package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Rules;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.RulesRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TripRepository;

@Slf4j
@AllArgsConstructor
@Service
public class RulesService implements IRulesService {
    @Autowired
    private RulesRepository rulesRepository;
    private TripRepository tripRepository;

    public Rules createRule(Rules rule, Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        rule.setTrip(trip);  // Affecter le Trip à la Rule
        return rulesRepository.save(rule);
    }


    public Rules updateRule(Integer ruleId, Rules updatedRule) {
        Rules existingRule = rulesRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        existingRule.setMaxWeight(updatedRule.getMaxWeight());
        existingRule.setCostPerKm(updatedRule.getCostPerKm());
        existingRule.setMaxPassengers(updatedRule.getMaxPassengers());
        existingRule.setAllowedLuggage(updatedRule.getAllowedLuggage());
        existingRule.setTimeRestrictions(updatedRule.getTimeRestrictions());
        existingRule.setMinDriverExperience(updatedRule.getMinDriverExperience());
        existingRule.setMinAge(updatedRule.getMinAge());
        existingRule.setVehicleTypeRestrictions(updatedRule.getVehicleTypeRestrictions());

        return rulesRepository.save(existingRule);
    }

    public void deleteRule(Integer ruleId) {
        rulesRepository.deleteById(ruleId);
    }

    public Rules getRuleById(Integer ruleId) {
        return rulesRepository.findById(ruleId).orElseThrow(() -> new RuntimeException("Rules not found"));
    }
}
