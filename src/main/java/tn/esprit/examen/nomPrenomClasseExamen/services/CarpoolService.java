package tn.esprit.examen.nomPrenomClasseExamen.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class CarpoolService implements ICarpoolService {
    private CarpoolRepository carpoolRepository;
    private SimpleUserRepository simpleUserRepository;
    private UserActivityService userActivityService;
    private CarpoolVisibilityService carpoolVisibilityService;
    private ObjectMapper objectMapper;

    @Override
    public Carpool ajouterCarpoolEtAffecterUser(Carpool carpool, Integer offerId) {
        SimpleUser offer = simpleUserRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        carpool.setSimpleUserOffer(offer);
        return carpoolRepository.save(carpool);
    }

    @Override
    public Carpool joinCarpool(Integer carpoolId, Integer userId, Integer numberOfPlaces) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (carpool.getSimpleUserJoin().contains(user)) {
            throw new RuntimeException("User has already joined this carpool!");
        }
        if (carpool.getCarpoolCapacity() < numberOfPlaces) {
            throw new RuntimeException("Not enough capacity for " + numberOfPlaces + " places!");
        }
        if (carpool.getSimpleUserOffer().getUserId().equals(userId)) {
            throw new RuntimeException("You cannot join your own carpool!");
        }
        if (numberOfPlaces <= 0) {
            throw new RuntimeException("Number of places must be positive!");
        }

        // Update simpleUserJoin
        carpool.getSimpleUserJoin().add(user);

        // Update joinedUsersPlaces
        Map<Integer, Integer> placesMap = getJoinedUsersPlaces(carpool);
        placesMap.put(userId, numberOfPlaces);
        setJoinedUsersPlaces(carpool, placesMap);

        // Update capacity
        carpool.setCarpoolCapacity(carpool.getCarpoolCapacity() - numberOfPlaces);
        updateCarpoolStatus(carpool);
        carpoolRepository.save(carpool);

        userActivityService.addPointsForJoin(user);
        return carpool;
    }

    @Override
    public void deleteCarpool(Integer carpoolId, Integer offerId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found"));
        if (!carpool.getSimpleUserOffer().getUserId().equals(offerId)) {
            throw new RuntimeException("You are not authorized to delete this carpool");
        }
        carpoolRepository.delete(carpool);
    }

    @Override
    public void leaveCarpool(Integer carpoolId, Integer userId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found"));
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!carpool.getSimpleUserJoin().contains(user)) {
            throw new RuntimeException("User is not in this carpool");
        }

        // Get numberOfPlaces from joinedUsersPlaces
        Map<Integer, Integer> placesMap = getJoinedUsersPlaces(carpool);
        Integer numberOfPlaces = placesMap.get(userId);
        if (numberOfPlaces == null) {
            throw new RuntimeException("No place allocation found for user");
        }

        // Update simpleUserJoin
        carpool.getSimpleUserJoin().remove(user);

        // Update joinedUsersPlaces
        placesMap.remove(userId);
        setJoinedUsersPlaces(carpool, placesMap);

        // Restore capacity
        carpool.setCarpoolCapacity(carpool.getCarpoolCapacity() + numberOfPlaces);
        updateCarpoolStatus(carpool);
        carpoolRepository.save(carpool);

        userActivityService.deductPointsForCancel(user);
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
    public List<Carpool> getFutureCarpools(Integer userId) {
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<Carpool> futureCarpools = carpoolRepository.findFutureCarpools(today, now, userId);
        List<Carpool> recommendedCarpools = getOrderedRecommendedCarpools(userId);
        Set<Integer> recommendedCarpoolIds = recommendedCarpools.stream()
                .map(Carpool::getCarpoolId)
                .collect(Collectors.toSet());
        return futureCarpools.stream()
                .filter(carpool -> !recommendedCarpoolIds.contains(carpool.getCarpoolId()) &&
                        (carpool.getSimpleUserOffer().getUserId().equals(userId) ||
                                carpoolVisibilityService.isVisibleForUser(carpool, user)))
                .collect(Collectors.toList());
    }

    @Override
    public SimpleUser getCarpoolBySimpleUserOffer(Integer carpoolId) {
        return carpoolRepository.findOffererByCarpoolId(carpoolId);
    }

    @Override
    public List<Carpool> getCarpoolsByUser(Integer userId) {
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        List<Carpool> userCarpools = carpoolRepository.findBySimpleUserOffer_UserId(userId);
        return userCarpools.stream()
                .filter(carpool -> carpool.getSimpleUserOffer().getUserId().equals(userId) ||
                        carpoolVisibilityService.isVisibleForUser(carpool, user))
                .collect(Collectors.toList());
    }

    @Override
    public Carpool updateCarpool(Integer carpoolId, Integer userId, Carpool updatedCarpool) {
        Carpool existingCarpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));
        if (!existingCarpool.getSimpleUserOffer().getUserId().equals(userId)) {
            throw new RuntimeException("You are not the owner of this carpool!");
        }
        if (!existingCarpool.getSimpleUserJoin().isEmpty()) {
            throw new RuntimeException("Cannot update carpool because other users have already joined!");
        }

        existingCarpool.setCarpoolDeparture(updatedCarpool.getCarpoolDeparture());
        existingCarpool.setCarpoolDestination(updatedCarpool.getCarpoolDestination());
        existingCarpool.setCarpoolDate(updatedCarpool.getCarpoolDate());
        existingCarpool.setCarpoolTime(updatedCarpool.getCarpoolTime());
        existingCarpool.setCarpoolCapacity(updatedCarpool.getCarpoolCapacity());
        existingCarpool.setCarpoolPrice(updatedCarpool.getCarpoolPrice());
        existingCarpool.setCarpoolCondition(updatedCarpool.getCarpoolCondition());
        return carpoolRepository.save(existingCarpool);
    }

    public void updateCarpoolStatus(Carpool carpool) {
        if (carpool.getCarpoolCapacity() == 0) {
            carpool.setCarpoolStatus(CarpoolStatus.unavailable);
        } else {
            carpool.setCarpoolStatus(CarpoolStatus.available);
        }
    }

    @Override
    public List<SimpleUser> getUsersWhoJoinedCarpool(Integer carpoolId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));
        return new ArrayList<>(carpool.getSimpleUserJoin());
    }

    @Override
    public List<Carpool> getCarpoolsJoinedByUser(Integer userId) {
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return new ArrayList<>(user.getCarpoolJoined());
    }

    @Override
    public List<Carpool> getOrderedRecommendedCarpools(Integer userId) {
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<Object[]> frequentRoutes = carpoolRepository.findFrequentRoutesByUser(userId);
        List<Carpool> allRecommended = new ArrayList<>();

        for (Object[] route : frequentRoutes) {
            String departure = (String) route[0];
            String destination = (String) route[1];
            List<Carpool> matchingCarpools = carpoolRepository.findFutureCarpoolsByRoute(
                    today, now, departure, destination, userId);
            matchingCarpools.stream()
                    .filter(carpool -> carpoolVisibilityService.isVisibleForUser(carpool, user))
                    .forEach(allRecommended::add);
        }
        return allRecommended;
    }

    private Map<Integer, Integer> getJoinedUsersPlaces(Carpool carpool) {
        try {
            if (carpool.getJoinedUsersPlaces() == null || carpool.getJoinedUsersPlaces().isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(carpool.getJoinedUsersPlaces(), new TypeReference<Map<Integer, Integer>>() {});
        } catch (Exception e) {
            log.error("Failed to parse joinedUsersPlaces", e);
            throw new RuntimeException("Error parsing joined users places");
        }
    }

    private void setJoinedUsersPlaces(Carpool carpool, Map<Integer, Integer> placesMap) {
        try {
            carpool.setJoinedUsersPlaces(placesMap.isEmpty() ? null : objectMapper.writeValueAsString(placesMap));
        } catch (Exception e) {
            log.error("Failed to serialize joinedUsersPlaces", e);
            throw new RuntimeException("Error serializing joined users places");
        }
    }
}