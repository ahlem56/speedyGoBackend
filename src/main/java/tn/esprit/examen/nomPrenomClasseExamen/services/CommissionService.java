package tn.esprit.examen.nomPrenomClasseExamen.services;
import lombok.RequiredArgsConstructor;
import  lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.CommissionRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.PartnersRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommissionService {
    private final PaymentRepository paymentRepository;
    private final PartnersRepository partnerRepository;
    private final CommissionRepository commissionRepository;

    @Scheduled(cron = "0 * * * * ?")
    public void calculateDailyCommissions() {
        List<Payment> payments = paymentRepository.findByCommissionCalculatedFalse();

        payments.forEach(payment -> {
            try {
                Partners partner = payment.getPartner();

                // Add null checks for critical dependencies
                if (partner == null) {
                    log.warn("Skipping payment {} - no associated partner", payment.getPaymentId());
                    return;
                }

                if (partner.getCommissionRate() == null) {
                    log.error("Partner {} has no commission rate set", partner.getPartnerId());
                    return;
                }

                if (payment.getPaymentAmount() == null) {
                    log.error("Payment {} has no amount specified", payment.getPaymentId());
                    return;
                }

                Commission commission = new Commission();
                commission.setPartner(partner);
                commission.setPayment(payment);

                BigDecimal commissionAmount = payment.getPaymentAmount()
                        .multiply(partner.getCommissionRate())
                        .setScale(2, RoundingMode.HALF_UP);

                commission.setAmount(commissionAmount);
                commission.setCalculatedAt(LocalDateTime.now());

                commissionRepository.save(commission);

                // Update partner's total
                partner.setTotalCommission(
                        partner.getTotalCommission() == null ?
                                commissionAmount :
                                partner.getTotalCommission().add(commissionAmount)
                );
                partnerRepository.save(partner);

                // Mark payment as processed
                payment.setCommissionCalculated(true);
                paymentRepository.save(payment);

            } catch (Exception e) {
                log.error("Error processing payment {}: {}", payment.getPaymentId(), e.getMessage());
            }});}
    public List<Commission> getCommissionsForPartner(Long partnerId) {
        return commissionRepository.findByPartnerId(partnerId);
    }

    public Map<String, BigDecimal> getCommissionSummary(Long partnerId) {
        List<Commission> commissions = commissionRepository.findByPartnerId(partnerId);

        BigDecimal total = commissions.stream()
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pending = commissions.stream()
                .filter(c -> !c.isPaidOut())
                .map(Commission::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "total", total,
                "pending", pending
        );
    }
    //ghalta mink
    //hjkfkdjjhdf


}
