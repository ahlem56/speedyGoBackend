package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;

import java.util.List;

public interface ParcelRepository extends JpaRepository<Parcel, Integer> {
  List<Parcel> findByDriver_Id(Long driverId); // Utiliser 'driver' au lieu de 'assignedDriver'

}
