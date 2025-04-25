package tn.esprit.examen.nomPrenomClasseExamen.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.PaymentRequest;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;

import tn.esprit.examen.nomPrenomClasseExamen.repositories.ParcelRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.PaymentRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.TripRepository;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final TripRepository tripRepository;
    private final ParcelRepository parcelRepository;

    public PaymentService(PaymentRepository paymentRepository, TripRepository tripRepository, ParcelRepository parcelRepository) {
        this.paymentRepository = paymentRepository;
        this.tripRepository = tripRepository;
        this.parcelRepository = parcelRepository;
        Stripe.apiKey = "sk_test_51Qx4HqRtzrEMIcCe68S8vL8kuWICgv7rI2hga1OI7oDdcv9aRamQbSMmsYI5qLFG0oSWq9KyoblmZgL2TIAUuBMc00jPaF2SSB";
    }

    public Payment createPayment(Payment payment, String sourceId) {
        try {
            ChargeCreateParams params =
                ChargeCreateParams.builder()
                    .setAmount((long) (payment.getPaymentAmount() * 100)) // Convert to cents
                    .setCurrency("usd")
                    .setDescription("Payment for Trip")
                    .setSource(sourceId) // Use the dynamic source ID
                    .build();

            Charge charge = Charge.create(params);
            payment.setStripeChargeId(charge.getId());
            return paymentRepository.save(payment);
        } catch (StripeException e) {
            throw new RuntimeException("Stripe payment failed", e);
        }
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public void deletePayment(Integer id) {
        paymentRepository.deleteById(id);
    }

    // Original method commented out
    // public Payment assignOrCreatePaymentToTrip(Integer tripId, Payment paymentDetails) {
    //     if (paymentDetails == null ||
    //             paymentDetails.getPaymentMethod() == null ||
    //             paymentDetails.getPaymentAmount() == null ||
    //             paymentDetails.getPaymentDate() == null) {
    //         throw new RuntimeException("Payment details are incomplete or invalid.");
    //     }
    //
    //     Trip trip = tripRepository.findById(tripId)
    //             .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));
    //
    //     Payment payment;
    //
    //     if (trip.getPayment() != null) {
    //         payment = trip.getPayment();
    //         payment.setPaymentAmount(paymentDetails.getPaymentAmount());
    //         payment.setPaymentMethod(paymentDetails.getPaymentMethod());
    //         payment.setPaymentDate(new Date());
    //     } else {
    //         payment = new Payment();
    //         payment.setPaymentAmount(paymentDetails.getPaymentAmount());
    //         payment.setPaymentMethod(paymentDetails.getPaymentMethod());
    //         payment.setPaymentDate(new Date());
    //         payment = paymentRepository.save(payment);
    //         payment.setTrip(trip);
    //     }
    //
    //     trip.setPayment(payment);
    //     tripRepository.save(trip);
    //     return payment;
    // }

    // New method implementation
    public Payment assignOrCreatePaymentToTrip(Integer tripId, Payment paymentDetails) {
        // Validate input
        if (paymentDetails == null || 
            paymentDetails.getPaymentMethod() == null || 
            paymentDetails.getPaymentAmount() == null) {
            throw new IllegalArgumentException("Invalid payment details");
        }

        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

        Payment payment = trip.getPayment();

        if (payment != null) {
            // Update existing payment
            payment.setPaymentAmount(paymentDetails.getPaymentAmount());
            payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        } else {
            // Create and assign new payment
            payment = new Payment();
            payment.setPaymentAmount(paymentDetails.getPaymentAmount());
            payment.setPaymentMethod(paymentDetails.getPaymentMethod());
            payment.setPaymentDate(new Date());
            payment = paymentRepository.save(payment);
            payment.setTrip(trip);
        }

        trip.setPayment(payment);
        tripRepository.save(trip);
        return payment;
    }

    // Original method commented out
    // public Payment assignParcelToPayment(Integer paymentId, Integer parcelId) {
    //     Payment payment = paymentRepository.findById(paymentId).orElse(null);
    //     Parcel parcel = parcelRepository.findById(parcelId).orElse(null);
    //     if (payment != null && parcel != null) {
    //         payment.setParcel(parcel);
    //         paymentRepository.save(payment);
    //     }
    //     return payment;
    // }

    // New method implementation
    public Payment assignParcelToPayment(Integer paymentId, Integer parcelId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        Parcel parcel = parcelRepository.findById(parcelId).orElse(null);

        if (parcel == null) {
            // Create a new Parcel if it doesn't exist
            parcel = new Parcel();
            parcel.setParcelId(parcelId);
            parcel = parcelRepository.save(parcel);
        }

        // Assign the parcel to the payment
        payment.setParcel(parcel);
        return paymentRepository.save(payment);
    }

    // Method to process a test payment using Stripe
    public Payment processTestPayment(PaymentRequest paymentRequest) {
        try {
            ChargeCreateParams params =
                ChargeCreateParams.builder()
                    .setAmount((long) (paymentRequest.getPayment().getPaymentAmount() * 100)) // Convert to cents
                    .setCurrency("usd")
                    .setDescription("Test Payment")
                    .setSource(paymentRequest.getSourceId()) // Use the dynamic source ID
                    .build();

            Charge charge = Charge.create(params);
            Payment payment = paymentRequest.getPayment();
            payment.setStripeChargeId(charge.getId());
            return paymentRepository.save(payment);
        } catch (StripeException e) {
            throw new RuntimeException("Stripe test payment failed", e);
        }
    }
}
