package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Commission;

import java.util.List;

public  interface CommissionRepository extends JpaRepository<Commission, Long> {

    @Query("SELECT c FROM Commission c WHERE c.partner.partnerId = :partnerId")
    List<Commission> findByPartnerId(@Param("partnerId") Long partnerId);

}
