package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Carpool;

public interface CarpoolRepository extends JpaRepository<Carpool, Integer> {
}
