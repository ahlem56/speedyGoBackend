package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.dto.PaymentRequestDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.PaymentRequest;

import java.util.List;

public interface IPaymentService {
    Payment createPayment(Payment payment, String sourceId);
    List<Payment> getAllPayments();
    Payment getPaymentById(Integer id);
    void deletePayment(Integer id);
    Payment assignOrCreatePaymentToTrip(Integer tripId, Payment paymentDetails);
    Payment assignParcelToPayment(Integer paymentId, Integer parcelId);
    Payment processTestPayment(PaymentRequest paymentRequest);
    String createPaymentIntent(PaymentRequestDTO request) throws com.stripe.exception.StripeException;
    Payment processPayment(PaymentRequestDTO request);
    List<Payment> getUserPaymentHistory(Integer userId); // Ensure this is present
}