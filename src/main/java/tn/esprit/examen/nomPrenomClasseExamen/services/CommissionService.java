package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.CommissionRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.PartnersRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommissionService {
    private final PaymentRepository paymentRepository;
    private final PartnersRepository partnerRepository;
    private final CommissionRepository commissionRepository;

    @Transactional
    public Commission createCommission(Integer partnerId, BigDecimal amount, String description) {
        log.info("Creating commission for partner ID: {}, amount: {}, description: {}", partnerId, amount, description);
        if (partnerId == null) {
            throw new IllegalArgumentException("Partner ID cannot be null");
        }
        Partners partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("Partner not found with ID: " + partnerId));
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Commission amount must be greater than zero");
        }
        Commission commission = new Commission();
        commission.setPartner(partner);
        commission.setAmount(amount);
        commission.setCalculatedAt(LocalDateTime.now());
        commission.setPaidOut(false);
        commission.setDescription(description);
        BigDecimal currentTotal = partner.getTotalCommission();
        if (currentTotal == null) {
            currentTotal = BigDecimal.ZERO;
        }
        partner.setTotalCommission(currentTotal.add(amount));
        partnerRepository.save(partner);
        log.info("Saving commission for partner: {}", partner.getPartnerName());
        return commissionRepository.save(commission);
    }

    public List<tn.esprit.examen.nomPrenomClasseExamen.dto.CommissionDTO> getAllCommissions() {
        return commissionRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void calculateDailyCommissions() {
        log.info("Starting daily commission calculation");
        List<Payment> payments = paymentRepository.findByCommissionCalculatedFalse();
        log.info("Found {} payments to process", payments.size());

        for (Payment payment : payments) {
            try {
                log.info("Processing payment ID: {}, partner_id: {}, amount: {}",
                        payment.getPaymentId(),
                        payment.getPartner() != null ? payment.getPartner().getPartnerId() : null,
                        payment.getPaymentAmount());

                Partners partner = payment.getPartner();
                if (partner == null) {
                    log.info("Payment {} has no associated partner, skipping", payment.getPaymentId());
                    payment.setCommissionCalculated(true);
                    paymentRepository.save(payment);
                    continue;
                }

                if (partner.getCommissionRate() == null) {
                    log.error("Partner {} has no commission rate set", partner.getPartnerId());
                    payment.setCommissionCalculated(true);
                    paymentRepository.save(payment);
                    continue;
                }

                if (payment.getPaymentAmount() == null) {
                    log.error("Payment {} has no amount specified", payment.getPaymentId());
                    payment.setCommissionCalculated(true);
                    paymentRepository.save(payment);
                    continue;
                }

                Commission commission = new Commission();
                commission.setPartner(partner);
                commission.setPayment(payment);
                commission.setPartnerId(partner.getPartnerId());
                commission.setPaymentId(payment.getPaymentId());

                BigDecimal commissionRate = partner.getCommissionRate();
                BigDecimal commissionAmount = payment.getPaymentAmount()
                        .multiply(commissionRate)
                        .setScale(2, RoundingMode.HALF_UP);

                commission.setAmount(commissionAmount);
                commission.setCalculatedAt(LocalDateTime.now());
                commission.setPaidOut(false);

                commissionRepository.save(commission);

                BigDecimal currentTotal = partner.getTotalCommission();
                if (currentTotal == null) {
                    currentTotal = BigDecimal.ZERO;
                }
                partner.setTotalCommission(currentTotal.add(commissionAmount));
                partnerRepository.save(partner);

                payment.setCommissionCalculated(true);
                paymentRepository.save(payment);
                log.info("Commission created for payment {}, amount: {}", payment.getPaymentId(), commissionAmount);
            } catch (Exception e) {
                log.error("Error processing payment {}: {}", payment.getPaymentId(), e.getMessage());
            }
        }
        log.info("Completed daily commission calculation");
    }

    public List<tn.esprit.examen.nomPrenomClasseExamen.dto.CommissionDTO> getCommissionsForPartner(Integer partnerId) {
        log.info("Getting commissions for partner ID: {}", partnerId);
        List<Commission> commissions = commissionRepository.findByPartnerId(partnerId);
        return commissions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Map<String, BigDecimal> getCommissionSummary(Integer partnerId) {
        log.info("Getting commission summary for partner ID: {}", partnerId);
        List<Commission> commissions = commissionRepository.findByPartnerId(partnerId);

        BigDecimal total = commissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pending = commissions.stream()
                .filter(c -> !c.isPaidOut())
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paid = total.subtract(pending);

        return Map.of(
                "total", total,
                "pending", pending,
                "paid", paid
        );
    }

    public tn.esprit.examen.nomPrenomClasseExamen.dto.CommissionDTO toDTO(Commission commission) {
        tn.esprit.examen.nomPrenomClasseExamen.dto.CommissionDTO dto = new tn.esprit.examen.nomPrenomClasseExamen.dto.CommissionDTO();
        dto.setCommissionId(commission.getCommissionId());
        dto.setPartnerId(commission.getPartner() != null ? commission.getPartner().getPartnerId() : null);
        dto.setPaymentId(commission.getPayment() != null ? commission.getPayment().getPaymentId() : null);
        dto.setAmount(commission.getAmount());
        dto.setPaidOut(commission.isPaidOut());
        dto.setDescription(commission.getDescription());
        dto.setCalculatedAt(commission.getCalculatedAt());
        dto.setUpdatedAt(commission.getUpdatedAt());
        if (commission.getPartner() != null) {
            dto.setPartnerName(commission.getPartner().getPartnerName());
        }
        return dto;
    }
}