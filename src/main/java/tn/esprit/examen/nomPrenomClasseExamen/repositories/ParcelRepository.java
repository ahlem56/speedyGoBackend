package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.util.Date;
import java.util.List;

public interface ParcelRepository extends JpaRepository<Parcel, Integer> {
  List<Parcel> findByDriver_Id(Long driverId); // Utiliser 'driver' au lieu de 'assignedDriver'
  List<Parcel> findBySimpleUser(SimpleUser simpleUser);
  // JPQL pour obtenir les colis créés après une certaine date
  @Query("SELECT p FROM Parcel p WHERE p.parcelDate > :date")
  List<Parcel> findParcelsAfterDate(@Param("date") Date date);

  // JPQL pour obtenir les colis créés avant une certaine date
  @Query("SELECT p FROM Parcel p WHERE p.parcelDate < :date")
  List<Parcel> findParcelsBeforeDate(@Param("date") Date date);

}
