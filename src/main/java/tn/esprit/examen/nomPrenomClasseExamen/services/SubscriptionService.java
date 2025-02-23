package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Subscription;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SubscriptionRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class SubscriptionService implements ISubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription createSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Optional<Subscription> getSubscriptionById(Integer id) {
        return subscriptionRepository.findById(id);
    }

    @Override
    public Subscription updateSubscription(Integer id, Subscription subscription) {
        if (subscriptionRepository.existsById(id)) {
            subscription.setSubscriptionId(id);
            return subscriptionRepository.save(subscription);
        }
        return null; // You could throw an exception if needed
    }

    @Override
    public boolean deleteSubscription(Integer id) {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
