package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Complaints;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.util.List;

@Repository
public interface ComplaintsRepository extends JpaRepository<Complaints, Integer> {
    List<Complaints> findBySimpleUser(SimpleUser simpleUser);

    @Query("SELECT c FROM Complaints c JOIN FETCH c.simpleUser")
    List<Complaints> findAllWithUsers();

}
