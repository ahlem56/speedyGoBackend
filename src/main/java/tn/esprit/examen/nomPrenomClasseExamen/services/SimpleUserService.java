package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Subscription;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SubscriptionRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class SimpleUserService implements ISimpleUserService {
    private SimpleUserRepository simpleUserRepository;
    private SubscriptionRepository subscriptionRepository;


    public Optional<SimpleUser> getUserById(Integer userId) {
        return simpleUserRepository.findById(userId);
    }

    @Override
    public void addSubscriptionToUser(Integer userId, Integer subscriptionId) {
        SimpleUser user = simpleUserRepository.findById(userId).orElseThrow();
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow();


        // Check if user is already subscribed
        if (user.getSubscription() != null) {
            throw new IllegalStateException("User is already subscribed to a subscription.");
        }
        // Link subscription to user
        user.setSubscription(subscription);
        user.setSubscriptionStartDate(LocalDate.now());

        // Save the user with updated subscription
        simpleUserRepository.save(user);
    }

    // Add the save method to persist users
    public SimpleUser save(SimpleUser user) {
        return simpleUserRepository.save(user);
    }


    // Method to fetch statistics for carpool offers posted by users
    public Map<Integer, Integer> getCarpoolOfferStatistics() {
        List<SimpleUser> users = simpleUserRepository.findAll();
        Map<Integer, Integer> offerStats = new HashMap<>();

        for (SimpleUser user : users) {
            offerStats.put(user.getUserId(), user.getCarpolingDoneSuser()); // Get number of carpool offers posted
        }
        return offerStats;
    }

}
