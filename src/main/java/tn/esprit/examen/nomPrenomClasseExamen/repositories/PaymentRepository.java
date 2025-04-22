package tn.esprit.examen.nomPrenomClasseExamen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByCommissionCalculatedFalse();
    List<Payment> findAllByOrderByPaymentDateDesc();
    @Query("SELECT p FROM Payment p WHERE p.user = :user ORDER BY p.paymentDate DESC")
    List<Payment> findByUserOrderByPaymentDateDesc(@Param("user") User user);
}
