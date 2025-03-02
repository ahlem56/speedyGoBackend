package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Carpool;
import tn.esprit.examen.nomPrenomClasseExamen.entities.CarpoolStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.CarpoolRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CarpoolService implements ICarpoolService {
    private CarpoolRepository carpoolRepository;
    private SimpleUserRepository simpleUserRepository;

    //user fait creation covoiturage
    @Override
    public Carpool ajouterCarpoolEtAffecterUser(Carpool carpool, Integer OfferId){
        SimpleUser Offer = simpleUserRepository.findById(OfferId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        carpool.setSimpleUserOffer(Offer);
        return carpoolRepository.save(carpool);
    }

    //user peut rejoindre un covoiturage
    @Override
    public Carpool joinCarpool(Integer carpoolId, Integer userId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));

        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Vérifie si l'utilisateur a déjà rejoint ce covoiturage
        if (carpool.getSimpleUserJoin().contains(user)) {
            throw new RuntimeException("User has already joined this carpool!");
        }

        // Vérifie si le covoiturage est plein
        if (carpool.getCarpoolCapacity() <= 0) {
            throw new RuntimeException("Carpool is full!");
        }

        // Vérifie si l'utilisateur est le créateur du covoiturage
        if (carpool.getSimpleUserOffer().getUserId().equals(userId)) {
            throw new RuntimeException("You cannot join your own carpool!");
        }


        // Ajouter l'utilisateur et réduire la capacité du covoiturage
        carpool.getSimpleUserJoin().add(user);
        carpool.setCarpoolCapacity(carpool.getCarpoolCapacity() - 1);

        updateCarpoolStatus(carpool);


        return carpoolRepository.save(carpool);
    }


    // mise à jour un covoiturage
   /* @Override
    public Carpool updateCarpool(Integer carpoolId, Integer OfferId, Carpool updatedCarpool) {
        Carpool existingCarpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));

        if (!existingCarpool.getSimpleUserOffer().getUserId().equals(OfferId)) {
            throw new RuntimeException("You are not authorized to update this carpool!");
        }

        existingCarpool.setCarpoolDeparture(updatedCarpool.getCarpoolDeparture());
        existingCarpool.setCarpoolDestination(updatedCarpool.getCarpoolDestination());
        existingCarpool.setCarpoolDate(updatedCarpool.getCarpoolDate());
        existingCarpool.setCarpoolCapacity(updatedCarpool.getCarpoolCapacity());
        existingCarpool.setCarpoolCondition(updatedCarpool.getCarpoolCondition());
        existingCarpool.setCarpoolPrice(updatedCarpool.getCarpoolPrice());
        existingCarpool.setLicensePlate(updatedCarpool.getLicensePlate());

        return carpoolRepository.save(existingCarpool);
    }*/
//user peut supprimer le carpool
    @Override
    public void deleteCarpool(Integer carpoolId, Integer offerId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found"));

        if (!carpool.getSimpleUserOffer().getUserId().equals(offerId)) {
            throw new RuntimeException("You are not authorized to delete this carpool");
        }

        carpoolRepository.delete(carpool);
    }

    //user can leave carpool
    @Override
    public void leaveCarpool(Integer carpoolId, Integer userId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found"));

        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!carpool.getSimpleUserJoin().contains(user)) {
            throw new RuntimeException("User is not in this carpool");
        }

        carpool.getSimpleUserJoin().remove(user);
        carpool.setCarpoolCapacity(carpool.getCarpoolCapacity() + 1);

        updateCarpoolStatus(carpool);

        carpoolRepository.save(carpool);
    }


    @Override
    public List<Carpool> getAllCarpools() {
        return carpoolRepository.findAll();
    }

    @Override
    public Carpool getCarpoolById(Integer carpoolId) {
        return carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));
    }

    @Override
    public List<Carpool> getFutureCarpools() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return carpoolRepository.findFutureCarpools(today, now);
    }

    //hethi bl carpool ettala3 simpleuser staamltha fl detail component
    @Override
    public SimpleUser getCarpoolBySimpleUserOffer(Integer carpoolId) {
        return carpoolRepository.findOffererByCarpoolId(carpoolId);
    }

    //hethi bl simpleuser ettala3 l carpool staamltha fl offer component
    @Override
    public List<Carpool> getCarpoolsByUser(Integer userId) {
        return carpoolRepository.findBySimpleUserOffer_UserId(userId);
    }

    @Override
    public Carpool updateCarpool(Integer carpoolId, Integer userId, Carpool updatedCarpool) {
        Carpool existingCarpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));

        // Vérifier que l'utilisateur est bien l'offreur du covoiturage
        if (!existingCarpool.getSimpleUserOffer().getUserId().equals(userId)) {
            throw new RuntimeException("You are not the owner of this carpool!");
        }

        // Vérifier que personne n'a encore rejoint le covoiturage
        if (!existingCarpool.getSimpleUserJoin().isEmpty()) {
            throw new RuntimeException("Cannot update carpool because other users have already joined!");
        }

        // Mettre à jour uniquement les champs modifiables
        existingCarpool.setCarpoolDeparture(updatedCarpool.getCarpoolDeparture());
        existingCarpool.setCarpoolDestination(updatedCarpool.getCarpoolDestination());
        existingCarpool.setCarpoolDate(updatedCarpool.getCarpoolDate());
        existingCarpool.setCarpoolTime(updatedCarpool.getCarpoolTime());
        existingCarpool.setCarpoolCapacity(updatedCarpool.getCarpoolCapacity());
        existingCarpool.setCarpoolPrice(updatedCarpool.getCarpoolPrice());
        existingCarpool.setCarpoolCondition(updatedCarpool.getCarpoolCondition());


        return carpoolRepository.save(existingCarpool);
    }


//methode bch ki tsir ay action l status yetbadel tous depends menha (join/leave /update)
    public void updateCarpoolStatus(Carpool carpool) {
        if (carpool.getCarpoolCapacity() == 0) {
            carpool.setCarpoolStatus(CarpoolStatus.unavailable);
        } else {
            carpool.setCarpoolStatus(CarpoolStatus.available);
        }
    }

    //liste mtaa simple users li aamlou join pour un carpool specifique
    @Override
    public List<SimpleUser> getUsersWhoJoinedCarpool(Integer carpoolId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));

        return new ArrayList<>(carpool.getSimpleUserJoin()); // Convertir Set en List
    }
}
