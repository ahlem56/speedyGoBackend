package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Carpool;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CarpoolRepository extends JpaRepository<Carpool, Integer> {

    @Query("SELECT c FROM Carpool c " +
            "WHERE (c.carpoolDate > :currentDate OR (c.carpoolDate = :currentDate AND c.carpoolTime > :currentTime)) " +
            "AND c.carpoolCapacity > 0 " +
            "AND c.simpleUserOffer.userId <> :userId " +
            "AND NOT EXISTS (SELECT u FROM c.simpleUserJoin u WHERE u.userId = :userId)")
    List<Carpool> findFutureCarpools(@Param("currentDate") LocalDate currentDate,
                                     @Param("currentTime") LocalTime currentTime,
                                     @Param("userId") Integer userId);

    @Query("SELECT c.simpleUserOffer FROM Carpool c WHERE c.carpoolId = :carpoolId")
    SimpleUser findOffererByCarpoolId(@Param("carpoolId") Integer carpoolId);

    List<Carpool> findBySimpleUserOffer_UserId(Integer userId);

    @Query("SELECT c.carpoolDeparture, c.carpoolDestination, COUNT(c) as freq " +
            "FROM Carpool c JOIN c.simpleUserJoin u " +
            "WHERE u.userId = :userId " +
            "GROUP BY c.carpoolDeparture, c.carpoolDestination " +
            "ORDER BY freq DESC")
    List<Object[]> findFrequentRoutesByUser(@Param("userId") Integer userId);

    @Query("SELECT c FROM Carpool c " +
            "WHERE (c.carpoolDate > :today OR (c.carpoolDate = :today AND c.carpoolTime > :now)) " +
            "AND c.carpoolDeparture = :departure AND c.carpoolDestination = :destination " +
            "AND c.carpoolCapacity > 0 " +
            "AND c.simpleUserOffer.userId <> :userId " +
            "AND NOT EXISTS (SELECT u FROM c.simpleUserJoin u WHERE u.userId = :userId)")
    List<Carpool> findFutureCarpoolsByRoute(@Param("today") LocalDate today,
                                            @Param("now") LocalTime now,
                                            @Param("departure") String departure,
                                            @Param("destination") String destination,
                                            @Param("userId") Integer userId);
}