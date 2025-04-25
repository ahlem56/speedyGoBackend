package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.PaymentRequest;
import tn.esprit.examen.nomPrenomClasseExamen.services.PaymentService;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/payment")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
    

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest paymentRequest) {
        Payment payment = paymentRequest.getPayment();
        String sourceId = paymentRequest.getSourceId();
        return ResponseEntity.ok(paymentService.createPayment(payment, sourceId));
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/assign/{tripId}")
    public ResponseEntity<Payment> assignOrCreatePaymentToTrip(
            @PathVariable Integer tripId,
            @RequestBody Payment paymentDetails) {

        try {
            Payment payment = paymentService.assignOrCreatePaymentToTrip(tripId, paymentDetails);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }}







        @PutMapping("/{paymentId}/assign-parcel/{parcelId}")
    public ResponseEntity<Payment> assignParcelToPayment(@PathVariable Integer paymentId, @PathVariable Integer parcelId) {
        return ResponseEntity.ok(paymentService.assignParcelToPayment(paymentId, parcelId));
    }
}
