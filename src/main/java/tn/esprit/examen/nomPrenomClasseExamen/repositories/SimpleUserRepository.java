package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Partners;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.util.List;

public interface SimpleUserRepository extends JpaRepository<SimpleUser, Integer> {
    SimpleUser findByUserEmail(String email);
    List<SimpleUser> findByPartners(Partners partners);
}
