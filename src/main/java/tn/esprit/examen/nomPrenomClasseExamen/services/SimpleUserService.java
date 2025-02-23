package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Subscription;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SubscriptionRepository;

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

        // Link subscription to user
        user.setSubscription(subscription);

        // Save the user with updated subscription
        simpleUserRepository.save(user);
    }

}
