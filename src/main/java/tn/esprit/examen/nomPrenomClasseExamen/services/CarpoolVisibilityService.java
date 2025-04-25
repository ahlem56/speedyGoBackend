package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Carpool;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ActivityLevel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class CarpoolVisibilityService {

    public boolean isVisibleForUser(Carpool carpool, SimpleUser user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime carpoolDateTime = LocalDateTime.of(carpool.getCarpoolDate(), carpool.getCarpoolTime());
        long minutesUntilCarpool = ChronoUnit.MINUTES.between(now, carpoolDateTime);
        boolean isUrgent = minutesUntilCarpool < 30;

        switch (user.getActivityLevel()) {
            case TOP_ACTIVE:
                return true; // Visible immédiatement
            case CONTRIBUTEUR:
                if (isUrgent) {
                    return now.isAfter(carpool.getCreationTime().plusMinutes(1));
                } else {
                    return now.isAfter(carpool.getCreationTime().plusMinutes(5));
                }
            case INACTIF:
                if (isUrgent) {
                    return now.isAfter(carpool.getCreationTime().plusMinutes(2));
                } else {
                    return now.isAfter(carpool.getCreationTime().plusMinutes(10));
                }
            default:
                return false;
        }
    }
}