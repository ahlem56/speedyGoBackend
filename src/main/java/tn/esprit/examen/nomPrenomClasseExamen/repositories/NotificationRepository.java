package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Notification;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findBySimpleUserOrderByNotificationDateDesc(SimpleUser user);
}
