package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.Date;
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
    private static final BigDecimal SILVER_THRESHOLD = new BigDecimal("10000.00");
    private static final BigDecimal GOLD_THRESHOLD = new BigDecimal("20000.00");
    private static final BigDecimal COMMISSION_INCREASE_SILVER = new BigDecimal("0.05");
    private static final BigDecimal COMMISSION_INCREASE_GOLD = new BigDecimal("0.05");
    public Partners createPartner(Partners partner) {
        log.info("Creating partner: {}", partner);
        // Assign a default promotion if none exists
        if (partner.getPromotions() == null) {
            Promotions defaultPromotion = new Promotions();
            defaultPromotion.setPromotionTitle("Default Tier");
            defaultPromotion.setPromotionDescription("Default promotion for new partner");
            defaultPromotion.setPromotionDiscountPercentage(0.0f);
            defaultPromotion.setPromotionStartDate(new Date());
            defaultPromotion.setPromotionEndDate(Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            promotionsRepository.save(defaultPromotion);
            partner.setPromotions(defaultPromotion);
        }
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

    @Transactional
    public Partners checkAndPromotePartner(Integer partnerId) {
        log.info("Checking promotion eligibility for partner ID: {}", partnerId);
        Partners partner = getPartnerById(partnerId);
        if (partner == null) {
            log.warn("Partner with ID {} not found", partnerId);
            return null;
        }

        BigDecimal totalCommission = partner.getTotalCommission();
        Promotions currentPromotion = partner.getPromotions();

        // Set promotion dates (1-year validity)
        Date startDate = new Date();
        Date endDate = Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Determine the current promotion tier (if any)
        String currentTier = currentPromotion != null ? currentPromotion.getPromotionTitle() : null;

        // Use the current commissionRate as the base rate, with fallbacks for specific partners
        BigDecimal baseCommissionRate = partner.getCommissionRate() != null ? partner.getCommissionRate() : BigDecimal.ZERO;
        if (partner.getPartnerId().equals(1)) {
            baseCommissionRate = new BigDecimal("0.10"); // Initial rate for Partner 1
        } else if (partner.getPartnerId().equals(6)) {
            baseCommissionRate = new BigDecimal("0.00"); // Initial rate for Partner 2
        }

        if (totalCommission.compareTo(GOLD_THRESHOLD) >= 0) {
            // Promote to Gold tier if not already Gold
            if (!"Gold Tier Promotion".equals(currentTier)) {
                log.info("Promoting partner ID: {} to Gold tier", partnerId);
                if (currentPromotion != null) {
                    currentPromotion.setPromotionTitle("Gold Tier Promotion");
                    currentPromotion.setPromotionDescription("Reached $20,000 commission milestone");
                    currentPromotion.setPromotionDiscountPercentage(15.0f);
                    currentPromotion.setPromotionStartDate(startDate);
                    currentPromotion.setPromotionEndDate(endDate);
                    promotionsRepository.save(currentPromotion);
                } else {
                    Promotions newPromotion = new Promotions();
                    newPromotion.setPromotionTitle("Gold Tier Promotion");
                    newPromotion.setPromotionDescription("Reached $20,000 commission milestone");
                    newPromotion.setPromotionDiscountPercentage(15.0f);
                    newPromotion.setPromotionStartDate(startDate);
                    newPromotion.setPromotionEndDate(endDate);
                    promotionsRepository.save(newPromotion);
                    partner.setPromotions(newPromotion);
                }
                partner.setCommissionRate(baseCommissionRate.add(COMMISSION_INCREASE_SILVER).add(COMMISSION_INCREASE_GOLD));
            } else {
                log.info("Partner ID: {} already at Gold tier", partnerId);
                partner.setCommissionRate(baseCommissionRate.add(COMMISSION_INCREASE_SILVER).add(COMMISSION_INCREASE_GOLD));
            }
        } else if (totalCommission.compareTo(SILVER_THRESHOLD) >= 0) {
            // Promote to Silver tier if not already Silver or Gold
            if (!"Silver Tier Promotion".equals(currentTier) && !"Gold Tier Promotion".equals(currentTier)) {
                log.info("Promoting partner ID: {} to Silver tier", partnerId);
                if (currentPromotion != null) {
                    currentPromotion.setPromotionTitle("Silver Tier Promotion");
                    currentPromotion.setPromotionDescription("Reached $10,000 commission milestone");
                    currentPromotion.setPromotionDiscountPercentage(10.0f);
                    currentPromotion.setPromotionStartDate(startDate);
                    currentPromotion.setPromotionEndDate(endDate);
                    promotionsRepository.save(currentPromotion);
                } else {
                    Promotions newPromotion = new Promotions();
                    newPromotion.setPromotionTitle("Silver Tier Promotion");
                    newPromotion.setPromotionDescription("Reached $10,000 commission milestone");
                    newPromotion.setPromotionDiscountPercentage(10.0f);
                    newPromotion.setPromotionStartDate(startDate);
                    newPromotion.setPromotionEndDate(endDate);
                    promotionsRepository.save(newPromotion);
                    partner.setPromotions(newPromotion);
                }
                partner.setCommissionRate(baseCommissionRate.add(COMMISSION_INCREASE_SILVER));
            } else if ("Gold Tier Promotion".equals(currentTier)) {
                log.info("Partner ID: {} already at Gold tier, maintaining commission rate", partnerId);
                partner.setCommissionRate(baseCommissionRate.add(COMMISSION_INCREASE_SILVER).add(COMMISSION_INCREASE_GOLD));
            } else {
                log.info("Partner ID: {} already at Silver tier", partnerId);
                partner.setCommissionRate(baseCommissionRate.add(COMMISSION_INCREASE_SILVER));
            }
        } else {
            log.info("Partner ID: {} does not qualify for promotion yet. Total commission: {}", partnerId, totalCommission);
            // Keep the default promotion (e.g., "Default Tier") and base commission rate
            partner.setCommissionRate(baseCommissionRate);
        }

        partnersRepository.save(partner);
        return partnersRepository.findById(partnerId).orElse(partner);
    }

    @Scheduled(fixedRate = 86400000) // Runs every 24 hours
    @Transactional
    public void checkAndPromoteAllPartners() {
        log.info("Checking promotion eligibility for all partners");
        List<Partners> partners = partnersRepository.findAll();
        for (Partners partner : partners) {
            checkAndPromotePartner(partner.getPartnerId());
        }
    }
}