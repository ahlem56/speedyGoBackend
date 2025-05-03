package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.dto.PaymentRequestDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.services.IPaymentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
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
        }
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<Payment>> getUserPaymentHistory(@PathVariable Integer userId) {
        log.info("🧪 Fetching payment history for user ID: {}", userId);
        try {
            List<Payment> payments = paymentService.getUserPaymentHistory(userId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Failed to fetch payment history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{paymentId}/assign-parcel/{parcelId}")
    public ResponseEntity<Payment> assignParcelToPayment(@PathVariable Integer paymentId, @PathVariable Integer parcelId) {
        return ResponseEntity.ok(paymentService.assignParcelToPayment(paymentId, parcelId));
    }

    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(@Valid @RequestBody PaymentRequestDTO paymentRequest,
                                                 BindingResult bindingResult) {
        log.info("🧪 Creating PaymentIntent: {}", paymentRequest);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Unknown field";
                errors.put(fieldName, error.getDefaultMessage());
            });
            log.error("Validation errors: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            String clientSecret = paymentService.createPaymentIntent(paymentRequest);
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", clientSecret);
            log.info("✅ PaymentIntent created with client_secret: {}", clientSecret);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            log.error("Stripe PaymentIntent creation failed: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Stripe PaymentIntent creation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequestDTO paymentRequest,
                                            BindingResult bindingResult) {
        log.info("🧪 Processing payment: {}", paymentRequest);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Unknown field";
                errors.put(fieldName, error.getDefaultMessage());
            });
            log.error("Validation errors: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Payment payment = paymentService.processPayment(paymentRequest);
            log.info("✅ Payment processed with ID: {}", payment.getPaymentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (Exception e) {
            log.error("Failed to process payment: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}