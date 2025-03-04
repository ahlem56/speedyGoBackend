package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Carpool;
import tn.esprit.examen.nomPrenomClasseExamen.entities.CarpoolStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CarpoolRepository extends JpaRepository<Carpool, Integer> {

    @Query("SELECT c FROM Carpool c WHERE c.carpoolDate > :currentDate OR (c.carpoolDate = :currentDate AND c.carpoolTime > :currentTime)AND(c.carpoolCapacity >0)")
    List<Carpool> findFutureCarpools(LocalDate currentDate, LocalTime currentTime);

    @Query("SELECT c.simpleUserOffer FROM Carpool c WHERE c.carpoolId = :carpoolId")
    SimpleUser findOffererByCarpoolId(@Param("carpoolId") Integer carpoolId);

    List<Carpool> findBySimpleUserOffer_UserId(Integer userId);




}
