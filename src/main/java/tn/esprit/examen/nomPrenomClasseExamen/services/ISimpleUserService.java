package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.util.List;

public interface ISimpleUserService {
    public void addSubscriptionToUser(Integer userId, Integer subscriptionId);
    List<SimpleUser> getAllSimpleUsers();
    SimpleUser getSimpleUserById(Integer id);
    void deleteSimpleUser(Integer id);
}
