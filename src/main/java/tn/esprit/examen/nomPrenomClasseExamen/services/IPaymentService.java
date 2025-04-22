package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.PaymentRequest;

import java.util.List;

public interface IPaymentService {
    List<Payment> getUserPaymentHistory();
}
