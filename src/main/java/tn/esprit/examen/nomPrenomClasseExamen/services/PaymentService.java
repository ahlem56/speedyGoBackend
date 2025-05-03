package tn.esprit.examen.nomPrenomClasseExamen.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.dto.PaymentRequestDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
<<<<<<< HEAD

import tn.esprit.examen.nomPrenomClasseExamen.repositories.*;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.model.PaymentIntent;
=======
import tn.esprit.examen.nomPrenomClasseExamen.repositories.*;
>>>>>>> recoveryAhlem

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final TripRepository tripRepository;
    private final ParcelRepository parcelRepository;
    private final UserRepository userRepository;
    private final SimpleUserRepository simpleUserRepository;
<<<<<<< HEAD


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
=======
    private final PartnersRepository partnersRepository;

    @PostConstruct
    public void init() {
>>>>>>> recoveryAhlem
        Stripe.apiKey = "sk_test_51Qx4HqRtzrEMIcCe68S8vL8kuWICgv7rI2hga1OI7oDdcv9aRamQbSMmsYI5qLFG0oSWq9KyoblmZgL2TIAUuBMc00jPaF2SSB";
    }

    public PaymentService(PaymentRepository paymentRepository, SimpleUserRepository simpleUserRepository, TripRepository tripRepository, UserRepository userRepository, ParcelRepository parcelRepository, PartnersRepository partnersRepository) {
        this.paymentRepository = paymentRepository;
        this.tripRepository = tripRepository;
        this.parcelRepository = parcelRepository;
        this.userRepository = userRepository;
        this.simpleUserRepository = simpleUserRepository;
        this.partnersRepository = partnersRepository;
    }

    @Override
    public Payment createPayment(Payment payment, String sourceId) {
        try {
            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount(payment.getPaymentAmount().multiply(BigDecimal.valueOf(100)).longValue())
                    .setCurrency("usd")
                    .setDescription("Payment for SpeedyGo")
                    .setSource(sourceId)
                    .build();

            Charge charge = Charge.create(params);
            payment.setStripeChargeId(charge.getId());
            return paymentRepository.save(payment);
        } catch (Exception e) {
            throw new RuntimeException("Stripe charge failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    public void deletePayment(Integer id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public Payment assignOrCreatePaymentToTrip(Integer tripId, Payment paymentDetails) {
        if (paymentDetails == null || paymentDetails.getPaymentMethod() == null || paymentDetails.getPaymentAmount() == null) {
            throw new IllegalArgumentException("Invalid payment details");
        }

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

        Payment payment = new Payment();
        payment.setPaymentAmount(paymentDetails.getPaymentAmount());
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        payment.setPaymentDate(new Date());
        payment.setTrip(trip);
        payment.setCommissionCalculated(false);

        payment = paymentRepository.save(payment);

        if (trip.getPayments() == null) {
            trip.setPayments(new LinkedHashSet<>());
        }
        trip.getPayments().add(payment);

        tripRepository.save(trip);
        return payment;
    }

    @Override
    public Payment assignParcelToPayment(Integer paymentId, Integer parcelId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + parcelId));

        payment.setParcel(parcel);
        return paymentRepository.save(payment);
    }

    @Override
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
                    .setAmount(payment.getPaymentAmount().multiply(BigDecimal.valueOf(100)).longValueExact())
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

    @Override
    public String createPaymentIntent(PaymentRequestDTO request) throws StripeException {
        Stripe.apiKey = "sk_test_51Qx4HqRtzrEMIcCe68S8vL8kuWICgv7rI2hga1OI7oDdcv9aRamQbSMmsYI5qLFG0oSWq9KyoblmZgL2TIAUuBMc00jPaF2SSB";

        BigDecimal amount = request.getPaymentAmount();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact())
                .setCurrency("USD")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }

<<<<<<< HEAD

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
=======
    @Override
    public Payment processPayment(PaymentRequestDTO request) {
        log.info("Processing payment: {}", request);

        if (request.getPaymentAmount() == null || request.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid payment amount");
            throw new IllegalArgumentException("Payment amount is required and must be positive");
        }
        if (request.getPaymentMethod() == null) {
            log.error("Payment method is null");
            throw new IllegalArgumentException("Payment method is required");
        }
        if (request.getStripePaymentMethodId() == null && request.getPaymentMethod() == PaymentMethod.STRIPE) {
            log.error("Stripe payment method ID is missing");
            throw new IllegalArgumentException("Stripe payment method ID is required");
        }
        if (request.getTripId() == null && request.getParcelId() == null) {
            log.error("Both Trip ID and Parcel ID are missing");
            throw new IllegalArgumentException("Either Trip ID or Parcel ID is required");
        }

        Payment existingPayment = null;
        if (request.getParcelId() != null) {
            existingPayment = paymentRepository.findByParcelParcelId(request.getParcelId());
            if (existingPayment != null) {
                log.info("Found existing payment {} for parcel {}", existingPayment.getPaymentId(), request.getParcelId());
                existingPayment.setPaymentAmount(request.getPaymentAmount());
                existingPayment.setPaymentMethod(request.getPaymentMethod());
                existingPayment.setPaymentDate(new Date());
                existingPayment.setLastUpdated(new Date());
                existingPayment.setStripeChargeId(request.getStripePaymentMethodId());
                existingPayment.setCommissionCalculated(false);
                return paymentRepository.save(existingPayment);
            }
        }

        Trip trip = null;
        if (request.getTripId() != null) {
            trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> {
                        log.error("Trip not found with ID: {}", request.getTripId());
                        return new RuntimeException("Trip not found with ID: " + request.getTripId());
                    });
        }

        Parcel parcel = null;
        if (request.getParcelId() != null) {
            parcel = parcelRepository.findById(request.getParcelId())
                    .orElseThrow(() -> {
                        log.error("Parcel not found with ID: {}", request.getParcelId());
                        return new RuntimeException("Parcel not found with ID: " + request.getParcelId());
                    });
        }

        SimpleUser user = null;
        Partners partner = null;
        if (request.getUserId() != null) {
            user = simpleUserRepository.findById(request.getUserId())
                    .orElseThrow(() -> {
                        log.error("User not found with ID: {}", request.getUserId());
                        return new RuntimeException("User not found with ID: " + request.getUserId());
                    });
            // Use user's partner if available
            partner = user.getPartners();
        }

        // If partnerId is provided, validate it
        if (request.getPartnerId() != null) {
            Partners providedPartner = partnersRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> {
                        log.error("Partner not found with ID: {}", request.getPartnerId());
                        return new RuntimeException("Partner not found with ID: " + request.getPartnerId());
                    });
            if (user != null && user.getPartners() != null && !user.getPartners().getPartnerId().equals(request.getPartnerId())) {
                log.error("Partner ID {} does not match user's partner ID {}", request.getPartnerId(), user.getPartners().getPartnerId());
                throw new IllegalArgumentException("Partner ID does not match user's partner association");
            }
            partner = providedPartner;
        }

        Payment payment = new Payment();
        payment.setPaymentAmount(request.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(new Date());
        payment.setLastUpdated(new Date());
        payment.setStripeChargeId(request.getStripePaymentMethodId());
        payment.setTrip(trip);
        payment.setParcel(parcel);
        payment.setPartner(partner); // Can be null
        payment.setUser(user);
        payment.setCommissionCalculated(false);

        try {
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment saved with ID: {}", savedPayment.getPaymentId());
            return savedPayment;
        } catch (Exception e) {
            log.error("Failed to save payment: {}", e.getMessage(), e);
>>>>>>> recoveryAhlem
            throw new RuntimeException("Failed to save payment: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Payment> getUserPaymentHistory(Integer userId) {
        log.info("🧪 Fetching payment history for user ID: {}", userId);
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("❌ User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });
        return paymentRepository.findByUser(user);
    }
}