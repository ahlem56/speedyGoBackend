package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.*;

import java.util.List;

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
        return partnersRepository.save(partner);
    }

    public List<Partners> getAllPartners() {
        return partnersRepository.findAll();
    }

    public Partners getPartnerById(Integer id) {
        return partnersRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deletePartner(Integer id) {
        Partners partner = partnersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

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
        // Check if the Partner exists
        Partners partner = partnersRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found with id: " + partnerId));

        // Check if the Promotion exists, if not, create a new one
        Promotions promotion = promotionsRepository.findById(promotionId)
                .orElseGet(() -> {
                    Promotions newPromotion = new Promotions();
                    newPromotion.setPromotionTitle(promotionDetails.getPromotionTitle());
                    newPromotion.setPromotionDescription(promotionDetails.getPromotionDescription());
                    newPromotion.setPromotionDiscountPercentage(promotionDetails.getPromotionDiscountPercentage());
                    newPromotion.setPromotionStartDate(promotionDetails.getPromotionStartDate());
                    newPromotion.setPromotionEndDate(promotionDetails.getPromotionEndDate());
                    return promotionsRepository.save(newPromotion);
                });

        // ✅ SET THE PROMOTION INSIDE PARTNER
        partner.setPromotions(promotion);  // 🔥 This line ensures the promotion is linked!

        // ✅ SAVE THE UPDATED PARTNER
        return partnersRepository.save(partner);
    }
}





