package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Partners;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Promotions;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.PartnersRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.PromotionsRepository;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PartnersService implements IPartnersService {

    private final PartnersRepository partnersRepository;
    private final PromotionsRepository promotionsRepository;

    public Partners createPartner(Partners partner) {
        return partnersRepository.save(partner);
    }

    public List<Partners> getAllPartners() {
        return partnersRepository.findAll();
    }

    public Partners getPartnerById(Integer id) {
        return partnersRepository.findById(id).orElse(null);
    }

    public void deletePartner(Integer id) {
        partnersRepository.deleteById(id);
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
                    promotionsRepository.save(newPromotion);
                    return newPromotion;
                });


        return partnersRepository.save(partner);
    }

}





