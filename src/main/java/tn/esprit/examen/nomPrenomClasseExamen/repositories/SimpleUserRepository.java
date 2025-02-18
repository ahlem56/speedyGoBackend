package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

public interface SimpleUserRepository extends JpaRepository<SimpleUser, Integer> {
    SimpleUser findByUserEmail(String email);
}
