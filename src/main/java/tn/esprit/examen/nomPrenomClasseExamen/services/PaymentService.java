package tn.esprit.examen.nomPrenomClasseExamen.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.dto.PaymentRequestDTO;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.*;

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
    private final PartnersRepository partnersRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          SimpleUserRepository simpleUserRepository,
                          TripRepository tripRepository,
                          UserRepository userRepository,
                          ParcelRepository parcelRepository,
                          PartnersRepository partnersRepository) {
        this.paymentRepository = paymentRepository;
        this.tripRepository = tripRepository;
        this.parcelRepository = parcelRepository;
        this.userRepository = userRepository;
        this.simpleUserRepository = simpleUserRepository;
        this.partnersRepository = partnersRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = "sk_test_51Qx4HqRtzrEMIcCe68S8vL8kuWICgv7rI2hga1OI7oDdcv9aRamQbSMmsYI5qLFG0oSWq9KyoblmZgL2TIAUuBMc00jPaF2SSB";
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

    @Transactional
    public Payment processPayment(PaymentRequestDTO request) {
        log.info("Processing payment: {}, partnerId: {}", request, request.getPartnerId());

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
                    .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + request.getTripId()));
        }

        Parcel parcel = null;
        if (request.getParcelId() != null) {
            parcel = parcelRepository.findById(request.getParcelId())
                    .orElseThrow(() -> new RuntimeException("Parcel not found with ID: " + request.getParcelId()));
        }

        SimpleUser user = null;
        if (request.getUserId() != null) {
            user = simpleUserRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));
            Hibernate.initialize(user.getPartners());
            log.info("User partners: {}", user.getPartners() != null ? user.getPartners().getPartnerId() : "null");
        }

        Partners partner = null;
        if (request.getPartnerId() != null) {
            partner = partnersRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + request.getPartnerId()));
            log.info("Partner ID {} assigned to payment for user ID: {}", request.getPartnerId(), request.getUserId());
        } else if (user != null && user.getPartners() != null) {
            partner = user.getPartners();
            log.info("Using user's partner ID {} for payment for user ID: {}", partner.getPartnerId(), request.getUserId());
        } else {
            log.warn("No partner assigned for payment for user ID: {}. Assigning default partner", request.getUserId());
            partner = partnersRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Default partner not found"));
            log.info("Assigned default partner ID 1 for user ID: {}", request.getUserId());
        }

        Payment payment = new Payment();
        payment.setPaymentAmount(request.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(new Date());
        payment.setLastUpdated(new Date());
        payment.setStripeChargeId(request.getStripePaymentMethodId());
        payment.setTrip(trip);
        payment.setParcel(parcel);
        payment.setPartner(partner);
        payment.setUser(user);
        payment.setCommissionCalculated(false);

        try {
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment saved with ID: {}, partner_id: {}, partner in object: {}",
                    savedPayment.getPaymentId(),
                    partner != null ? partner.getPartnerId() : "NULL",
                    savedPayment.getPartner() != null ? savedPayment.getPartner().getPartnerId() : "NULL");
            return savedPayment;
        } catch (Exception e) {
            log.error("Failed to save payment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save payment: " + e.getMessage(), e);
        }
    }
    @Override
    public List<Payment> getUserPaymentHistory(Integer userId) {
        log.info("Fetching payment history for user ID: {}", userId);
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return paymentRepository.findByUser(user);
    }
}
