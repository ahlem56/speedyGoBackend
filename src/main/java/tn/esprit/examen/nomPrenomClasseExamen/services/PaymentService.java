package tn.esprit.examen.nomPrenomClasseExamen.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.DTO.PaymentRequestDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;

import tn.esprit.examen.nomPrenomClasseExamen.repositories.*;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.model.PaymentIntent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final TripRepository tripRepository;
    private final ParcelRepository parcelRepository;
    private final UserRepository userRepository;
    private final SimpleUserRepository simpleUserRepository;


    @PostConstruct
    public void init() {
        Stripe.apiKey = "sk_test_51Qx4HqRtzrEMIcCe68S8vL8kuWICgv7rI2hga1OI7oDdcv9aRamQbSMmsYI5qLFG0oSWq9KyoblmZgL2TIAUuBMc00jPaF2SSB"; // ✅ Use your secret key
    }

    public PaymentService(PaymentRepository paymentRepository,SimpleUserRepository simpleUserRepository, TripRepository tripRepository,UserRepository userRepository, ParcelRepository parcelRepository) {
        this.paymentRepository = paymentRepository;
        this.tripRepository = tripRepository;
        this.parcelRepository = parcelRepository;
        this.userRepository = userRepository;
        this.simpleUserRepository = simpleUserRepository;
        Stripe.apiKey = "sk_test_51Qx4HqRtzrEMIcCe68S8vL8kuWICgv7rI2hga1OI7oDdcv9aRamQbSMmsYI5qLFG0oSWq9KyoblmZgL2TIAUuBMc00jPaF2SSB";
    }

    public Payment createPayment(Payment payment, String sourceId) {
        try {
            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount(payment.getPaymentAmount().multiply(BigDecimal.valueOf(100)).longValue()) // ✅ OK!
                    // cents
                    .setCurrency("usd")
                    .setDescription("Payment for SpeedyGo")
                    .setSource(sourceId) // ✅ token or payment method ID from frontend
                    .build();

            Charge charge = Charge.create(params);
            payment.setStripeChargeId(charge.getId());

            // 💾 Save your payment entity to the DB
            return paymentRepository.save(payment);

        } catch (Exception e) {
            throw new RuntimeException("Stripe charge failed: " + e.getMessage(), e);
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
    // Fix null payment handling in processTestPayment
    public Payment processTestPayment(PaymentRequest paymentRequest) {
        if (paymentRequest == null || paymentRequest.getPayment() == null) {
            throw new IllegalArgumentException("Payment request is invalid");
        }

        Payment payment = paymentRequest.getPayment();
        if (payment.getPaymentAmount() == null) {
            throw new IllegalArgumentException("Payment amount is required");
        }

        try {
            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount(payment.getPaymentAmount()
                            .multiply(BigDecimal.valueOf(100))
                            .longValueExact())
                    .setCurrency("USD")
                    .setDescription("Test Payment")
                    .setSource(paymentRequest.getSourceId())
                    .build();

            Charge charge = Charge.create(params);
            payment.setStripeChargeId(charge.getId());
            return paymentRepository.save(payment);
        } catch (StripeException e) {
            log.error("Stripe payment failed: {}", e.getMessage());
            throw new RuntimeException("Payment processing failed", e);
        }
    }
    public String createPaymentIntent(PaymentRequestDTO request) throws StripeException {
        Stripe.apiKey = "sk_test_51Qx4HqRtzrEMIcCe68S8vL8kuWICgv7rI2hga1OI7oDdcv9aRamQbSMmsYI5qLFG0oSWq9KyoblmZgL2TIAUuBMc00jPaF2SSB";

        // Convert Double to BigDecimal first
        BigDecimal amount = request.getPaymentAmount();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(
                        amount.multiply(BigDecimal.valueOf(100))  // Now we can use multiply
                                .setScale(0, RoundingMode.HALF_UP)      // Round to whole number
                                .longValueExact()                        // Convert to long
                )
                .setCurrency("USD")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }


    public Payment processPayment(PaymentRequestDTO request) {
        log.info("🧪 Payment Method DTO: {}", request.getPaymentMethod());
        log.info("🧪 Stripe ID: {}", request.getStripePaymentMethodId());
        log.info("🧪 Amount: {}", request.getPaymentAmount());
        log.info("🧪 User ID received: {}", request.getUserId());
        log.info("🧪 Trip ID received: {}", request.getTripId());

        Payment payment = new Payment();
        payment.setPaymentAmount(request.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(new Date());
        payment.setLastUpdated(new Date());
        payment.setStripeChargeId(request.getStripePaymentMethodId());

        // 🛠️ FIRST: Manual user assignment (priority)
        if (request.getUserId() != null) {
            log.info("🔍 Looking for SimpleUser with ID: {}", request.getUserId());
            SimpleUser user = simpleUserRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("SimpleUser not found with ID: " + request.getUserId()));
            log.info("✅ Found SimpleUser: {} {}", user.getUserFirstName(), user.getUserLastName());
            payment.setUser(user);
            log.info("✅ Set user in payment: {} {}", payment.getUser().getUserFirstName(), payment.getUser().getUserLastName());
        } else {
            log.warn("⚠️ No user ID provided in payment request");
        }

        // ✅ THEN: Associate Trip if exists
        if (request.getTripId() != null) {
            Trip trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + request.getTripId()));
            payment.setTrip(trip);

            if (trip.getSimpleUser() != null && payment.getUser() == null) { // Only if user is still null
                SimpleUser simpleUserFromTrip = trip.getSimpleUser();
                payment.setUser(simpleUserFromTrip);
                log.info("✅ Set user from trip: {} {}", simpleUserFromTrip.getUserFirstName(), simpleUserFromTrip.getUserLastName());

                if (simpleUserFromTrip.getPartners() != null) {
                    payment.setPartner(simpleUserFromTrip.getPartners());
                }
            }
        }

        // ✅ Optional: Associate Parcel if needed
        if (request.getParcelId() != null) {
            Parcel parcel = parcelRepository.findById(request.getParcelId())
                    .orElseThrow(() -> new RuntimeException("Parcel not found with ID: " + request.getParcelId()));
            payment.setParcel(parcel);
        }

        // ✅ Safe saving with explicit user check
        try {
            log.info("🔍 Final payment user before save: {}", 
                payment.getUser() != null ? 
                payment.getUser().getUserFirstName() + " " + payment.getUser().getUserLastName() : 
                "null");
            
            Payment savedPayment = paymentRepository.save(payment);
            
            log.info("✅ Saved payment with user: {}", 
                savedPayment.getUser() != null ? 
                savedPayment.getUser().getUserFirstName() + " " + savedPayment.getUser().getUserLastName() : 
                "null");
            
            return savedPayment;
        } catch (Exception e) {
            log.error("💥 Error while saving payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save payment: " + e.getMessage(), e);
        }
    }


    @Override
    public List<Payment> getUserPaymentHistory() {
        return paymentRepository.findAllByOrderByPaymentDateDesc();
    }
}
