package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Subscription;

import java.util.List;
import java.util.Optional;

public interface ISubscriptionService {
    public Subscription createSubscription(Subscription subscription);
    public List<Subscription> getAllSubscriptions() ;
    public Optional<Subscription> getSubscriptionById(Integer id);
    public Subscription updateSubscription(Integer id, Subscription subscription) ;
    public boolean deleteSubscription(Integer id);

}
