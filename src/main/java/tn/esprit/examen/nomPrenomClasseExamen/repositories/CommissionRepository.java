package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Commission;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Partners;

import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Integer> {

    List<Commission> findByPartnerId(Integer partnerId);
    
    @Query("SELECT c FROM Commission c WHERE c.partner.partnerId = :partnerId")
    List<Commission> findByPartnerIdInteger(@Param("partnerId") Integer partnerId);

    List<Commission> findByPartner(Partners partner);

}
