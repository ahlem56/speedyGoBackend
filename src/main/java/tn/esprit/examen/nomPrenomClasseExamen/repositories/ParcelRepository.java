package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Status;

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
  // Nombre de colis livrés par jour
  // Nombre de livraisons par jour
  // Nombre de livraisons pour un jour donné
  @Query("SELECT COUNT(p) FROM Parcel p WHERE DATE(p.parcelDate) = DATE(:date) AND p.status = 'DELIVERED'")
  long countDeliveredParcelsByDay(@Param("date") Date date);

  // Nombre de livraisons pour la semaine actuelle
  @Query("SELECT COUNT(p) FROM Parcel p WHERE YEARWEEK(p.parcelDate) = YEARWEEK(:date) AND p.status = 'DELIVERED'")
  long countDeliveredParcelsByWeek(@Param("date") Date date);

  // Nombre de livraisons pour le mois actuel
  @Query("SELECT COUNT(p) FROM Parcel p WHERE YEAR(p.parcelDate) = YEAR(:date) AND MONTH(p.parcelDate) = MONTH(:date) AND p.status = 'DELIVERED'")
  long countDeliveredParcelsByMonth(@Param("date") Date date);
  List<Parcel> findByStatusAndArchivedFalse(Status status);
  List<Parcel> findByStatus(Status status);  // Méthode pour récupérer les colis par statut
  List<Parcel> findByDamageImageUrlIsNotNull();  // Fetch parcels with damage reported

}
