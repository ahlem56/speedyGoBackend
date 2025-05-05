package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ActivityLevel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Rules;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.RulesRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class UserActivityService {

    @Autowired
    private SimpleUserRepository userRepository;

    @Autowired
    private RulesRepository rulesRepository;

    public void addPointsForJoin(SimpleUser user) {
        Rules rules = getCurrentRules();
        Integer currentPoints = user.getPoints() != null ? user.getPoints() : 0;
        user.setPoints(currentPoints + rules.getPointsForJoin());
        updateActivityLevel(user);
        user.setLastActiveDate(LocalDateTime.now());
        userRepository.save(user);
    }

    public void deductPointsForCancel(SimpleUser user) {
        Rules rules = getCurrentRules();
        user.setPoints(Math.max(0, user.getPoints() - rules.getPointsForCancel()));
        updateActivityLevel(user);
        user.setLastActiveDate(LocalDateTime.now());
        userRepository.save(user);
    }

    private void updateActivityLevel(SimpleUser user) {
        Rules rules = getCurrentRules();
        if (user.getPoints() >= rules.getTopActivePointsThreshold()) {
            user.setActivityLevel(ActivityLevel.TOP_ACTIVE);
        } else if (user.getPoints() >= rules.getContributeurPointsThreshold()) {
            user.setActivityLevel(ActivityLevel.CONTRIBUTEUR);
        } else {
            user.setActivityLevel(ActivityLevel.INACTIF);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void deductPointsForInactivity() {
        Rules rules = getCurrentRules();
        List<SimpleUser> users = userRepository.findAll();
        LocalDateTime today = LocalDateTime.now();
        for (SimpleUser user : users) {
            long daysInactive = ChronoUnit.DAYS.between(user.getLastActiveDate(), today);
            if (daysInactive > rules.getInactiveDaysThreshold()) {
                int pointsToDeduct = (int) (daysInactive - rules.getInactiveDaysThreshold()) * rules.getPointsDeductedPerInactiveDay();
                user.setPoints(Math.max(0, user.getPoints() - pointsToDeduct));
                updateActivityLevel(user);
                userRepository.save(user);
            }
        }
    }

    private Rules getCurrentRules() {
        return rulesRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No rules configuration found!"));
    }

    public Rules updateRules(Rules updatedRules) {
        Rules currentRules = rulesRepository.findAll().stream()
                .findFirst()
                .orElse(new Rules());
        currentRules.setPointsForJoin(updatedRules.getPointsForJoin());
        currentRules.setPointsForCancel(updatedRules.getPointsForCancel());
        currentRules.setPointsDeductedPerInactiveDay(updatedRules.getPointsDeductedPerInactiveDay());
        currentRules.setInactiveDaysThreshold(updatedRules.getInactiveDaysThreshold());
        currentRules.setTopActivePointsThreshold(updatedRules.getTopActivePointsThreshold());
        currentRules.setContributeurPointsThreshold(updatedRules.getContributeurPointsThreshold());
        return rulesRepository.save(currentRules);
    }

    public Rules createRules(Rules newRules) {
        if (rulesRepository.findAll().isEmpty()) {
            return rulesRepository.save(newRules);
        } else {
            throw new RuntimeException("Rules already exist! Use updateRules to modify existing rules.");
        }
    }

    public Rules getRules() {
        return rulesRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No rules configuration found!"));
    }
}