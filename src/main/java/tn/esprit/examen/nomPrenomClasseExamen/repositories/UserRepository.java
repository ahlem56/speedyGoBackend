package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.userEmail = :email")
    User findByUserEmail(@Param("email") String email);
    @Query("SELECT COUNT(u) FROM User u")
    Long countTotalUsers();
    User findByResetToken(String token);
}
