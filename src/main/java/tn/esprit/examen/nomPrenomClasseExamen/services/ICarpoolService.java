package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Carpool;
import tn.esprit.examen.nomPrenomClasseExamen.entities.CarpoolStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.util.List;

public interface ICarpoolService {

    Carpool ajouterCarpoolEtAffecterUser(Carpool carpool, Integer OfferId);
    public Carpool joinCarpool(Integer carpoolId, Integer userId, Integer numberOfPlaces) ;    //Carpool updateCarpool(Integer carpoolId, Integer OfferId, Carpool updatedCarpool);
    void leaveCarpool(Integer carpoolId, Integer userId);
    void deleteCarpool(Integer carpoolId, Integer offerId);
    List<Carpool> getAllCarpools();
    Carpool getCarpoolById(Integer carpoolId);
    SimpleUser getCarpoolBySimpleUserOffer(Integer carpoolId);
    List<Carpool> getCarpoolsByUser(Integer userId);
    Carpool updateCarpool(Integer carpoolId, Integer userId, Carpool updatedCarpool);
    void updateCarpoolStatus(Carpool carpool);
    List<SimpleUser> getUsersWhoJoinedCarpool(Integer carpoolId);
    List<Carpool> getCarpoolsJoinedByUser(Integer userId);

    List<Carpool> getOrderedRecommendedCarpools(Integer userId) ;

    public List<Carpool> getFutureCarpools(Integer userId) ;

}
