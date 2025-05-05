package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ComplaintStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Complaints;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.util.Date;
import java.util.List;

@Repository
public interface ComplaintsRepository extends JpaRepository<Complaints, Integer> {
    List<Complaints> findBySimpleUser(SimpleUser simpleUser);

    @Query("SELECT c FROM Complaints c JOIN FETCH c.simpleUser")
    List<Complaints> findAllWithUsers();

    @Query("SELECT c.simpleUser FROM Complaints c WHERE c.complaintId = :complaintId")
    SimpleUser findSimpleUserByComplaintId(@Param("complaintId") Integer complaintId);

    @Query("SELECT c FROM Complaints c WHERE c.complaintStatus = :status AND c.complaintCreationDate < :thresholdDate")
    List<Complaints> findByComplaintStatusAndCreationDateBefore(@Param("status") ComplaintStatus status, @Param("thresholdDate") Date thresholdDate);

    // Méthode pour déboguer toutes les réclamations pending
    List<Complaints> findByComplaintStatus(@Param("status") ComplaintStatus status);

    @Query("SELECT c FROM Complaints c JOIN FETCH c.simpleUser ORDER BY " +
            "CASE c.severity " +
            "WHEN 'high' THEN 1 " +
            "WHEN 'medium' THEN 2 " +
            "WHEN 'low' THEN 3 " +
            "ELSE 4 END")
    List<Complaints> findAllWithUsersSortedBySeverity();
}