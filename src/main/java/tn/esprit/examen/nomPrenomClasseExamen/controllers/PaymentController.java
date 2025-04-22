package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.DTO.PaymentRequestDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.exceptions.ResourceNotFoundException;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ParcelRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.PaymentRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TripRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.PaymentService;
import tn.esprit.examen.nomPrenomClasseExamen.entities.PaymentMethod;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequestMapping("/payments")
@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PaymentController {

    private final PaymentService paymentService;
    private final TripRepository tripRepository;
    private final ParcelRepository parcelRepository;
    private final PaymentRepository paymentRepository;

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

    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getPaymentHistory(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(paymentService.getUserPaymentHistory());
    }

    @PutMapping("/{paymentId}/assign-parcel/{parcelId}")
    public ResponseEntity<Payment> assignParcelToPayment(@PathVariable Integer paymentId, @PathVariable Integer parcelId) {
        return ResponseEntity.ok(paymentService.assignParcelToPayment(paymentId, parcelId));
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequestDTO request) {
        try {
            String clientSecret = paymentService.createPaymentIntent(request);
            return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(
            @Valid @RequestBody PaymentRequestDTO paymentRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation failed");
        }

        try {
            Stripe.apiKey = "sk_test_51Qx4HqRtzrEMIcCe68S8vL8kuWICgv7rI2hga1OI7oDdcv9aRamQbSMmsYI5qLFG0oSWq9KyoblmZgL2TIAUuBMc00jPaF2SSB";

            if (StringUtils.isBlank(paymentRequest.getStripePaymentMethodId())) {
                throw new IllegalArgumentException("Stripe payment method ID is required");
            }

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getPaymentAmount()
                            .multiply(BigDecimal.valueOf(100))
                            .longValueExact())
                    .setCurrency("usd")
                    .setPaymentMethod(paymentRequest.getStripePaymentMethodId())
                    .setConfirm(true)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Payment payment = new Payment();
            payment.setPaymentAmount(paymentRequest.getPaymentAmount());
            System.out.println("🧪 Payment Method DTO: " + paymentRequest.getPaymentMethod());
            System.out.println("🧪 Stripe ID: " + paymentRequest.getStripePaymentMethodId());
            System.out.println("🧪 Amount: " + paymentRequest.getPaymentAmount());
            payment.setPaymentMethod(paymentRequest.getPaymentMethod());
            payment.setPaymentDate(paymentRequest.getPaymentDate());
            payment.setLastUpdated(new Date());
            payment.setStripeChargeId(intent.getId());

            if (paymentRequest.getTripId() != null) {
                Trip trip = tripRepository.findById(paymentRequest.getTripId())
                        .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
                payment.setTrip(trip);
            }

            if (paymentRequest.getParcelId() != null) {
                Parcel parcel = parcelRepository.findById(paymentRequest.getParcelId())
                        .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));
                payment.setParcel(parcel);
            }

            Payment savedPayment = paymentRepository.save(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Stripe payment failed", "details", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}
