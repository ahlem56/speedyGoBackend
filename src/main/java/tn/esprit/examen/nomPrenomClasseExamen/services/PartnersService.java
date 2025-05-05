package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class PartnersService implements IPartnersService {

    private final PartnersRepository partnersRepository;
    private final PromotionsRepository promotionsRepository;
    private final SimpleUserRepository simpleUserRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final CommissionRepository commissionRepository;

    public Partners createPartner(Partners partner) {
        log.info("Creating partner: {}", partner);
        return partnersRepository.save(partner);
    }

    public List<Partners> getAllPartners() {
        log.info("Fetching all partners");
        return partnersRepository.findAll();
    }

    public Partners getPartnerById(Integer id) {
        log.info("Fetching partner with ID: {}", id);
        return partnersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + id));
    }

    public Partners updatePartner(Partners partner) {
        log.info("Updating partner: {}", partner);
        if (partner.getPartnerId() == null) {
            throw new RuntimeException("Partner ID is required for update");
        }
        if (!partnersRepository.existsById(partner.getPartnerId())) {
            throw new RuntimeException("Partner not found with ID: " + partner.getPartnerId());
        }
        return partnersRepository.save(partner);
    }

    @Transactional
    public void deletePartner(Integer id) {
        log.info("Deleting partner with ID: {}", id);
        Partners partner = partnersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + id));

        // Handle SimpleUser relationships
        List<SimpleUser> simpleUsers = simpleUserRepository.findByPartners(partner);
        for (SimpleUser user : simpleUsers) {
            user.setPartners(null);
            simpleUserRepository.save(user);
        }

        // Handle User relationships
        List<User> users = userRepository.findByPartner(partner);
        for (User user : users) {
            user.setPartner(null);
            userRepository.save(user);
        }

        // Handle Payment relationships
        List<Payment> payments = paymentRepository.findByPartner(partner);
        for (Payment payment : payments) {
            payment.setPartner(null);
            paymentRepository.save(payment);
        }

        // Handle Commission relationships
        List<Commission> commissions = commissionRepository.findByPartner(partner);
        for (Commission commission : commissions) {
            commission.setPartner(null);
            commissionRepository.save(commission);
        }

        // Finally delete the partner
        partnersRepository.delete(partner);
    }

    public Partners assignOrCreatePromotionToPartner(Integer partnerId, Integer promotionId, Promotions promotionDetails) {
        log.info("Assigning or creating promotion ID: {} for partner ID: {}", promotionId, partnerId);
        Partners partner = partnersRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + partnerId));

        Promotions promotion = promotionsRepository.findById(promotionId)
                .orElseGet(() -> {
                    log.info("Creating new promotion: {}", promotionDetails);
                    Promotions newPromotion = new Promotions();
                    newPromotion.setPromotionTitle(promotionDetails.getPromotionTitle());
                    newPromotion.setPromotionDescription(promotionDetails.getPromotionDescription());
                    newPromotion.setPromotionDiscountPercentage(promotionDetails.getPromotionDiscountPercentage());
                    newPromotion.setPromotionStartDate(promotionDetails.getPromotionStartDate());
                    newPromotion.setPromotionEndDate(promotionDetails.getPromotionEndDate());
                    return promotionsRepository.save(newPromotion);
                });

        partner.setPromotions(promotion);
        return partnersRepository.save(partner);
    }

    public Map<String, Double> getMonthlyPaymentRevenue() {
        log.info("Calculating monthly payment revenue");
        List<Payment> payments = paymentRepository.findAll();
        Map<String, Double> monthlyRevenue = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            monthlyRevenue.put(String.valueOf(i), 0.0);
        }

        for (Payment payment : payments) {
            if (payment != null && payment.getPaymentDate() != null && payment.getPaymentAmount() != null) {
                LocalDate localDate = payment.getPaymentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int monthValue = localDate.getMonthValue();
                Double currentRevenue = monthlyRevenue.getOrDefault(String.valueOf(monthValue), 0.0);
                monthlyRevenue.put(String.valueOf(monthValue), currentRevenue + payment.getPaymentAmount().doubleValue());
            } else {
                log.warn("Skipping null payment or payment data: {}", payment);
            }
        }

        log.info("Monthly revenue calculated: {}", monthlyRevenue);
        return monthlyRevenue;
    }

    public Map<String, Double> getDailyPaymentRevenue(int month, int year) {
        log.info("Calculating daily payment revenue for month: {}, year: {}", month, year);
        List<Payment> payments = paymentRepository.findAll();
        Map<String, Double> dailyRevenue = new HashMap<>();

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            dailyRevenue.put(String.valueOf(day), 0.0);
        }

        for (Payment payment : payments) {
            if (payment != null && payment.getPaymentDate() != null && payment.getPaymentAmount() != null) {
                LocalDate localDate = payment.getPaymentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (localDate.getYear() == year && localDate.getMonthValue() == month) {
                    int day = localDate.getDayOfMonth();
                    String dayKey = String.valueOf(day);
                    Double currentRevenue = dailyRevenue.getOrDefault(dayKey, 0.0);
                    dailyRevenue.put(dayKey, currentRevenue + payment.getPaymentAmount().doubleValue());
                }
            } else {
                log.warn("Skipping null payment or payment data: {}", payment);
            }
        }

        log.info("Daily revenue calculated: {}", dailyRevenue);
        return dailyRevenue;
    }
}