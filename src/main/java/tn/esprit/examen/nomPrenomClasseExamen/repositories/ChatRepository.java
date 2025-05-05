package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
}
