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
import java.time.LocalDateTime;
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
    private NotificationService notificationService;

    private ObjectMapper objectMapper;

    @Override
    public Carpool ajouterCarpoolEtAffecterUser(Carpool carpool, Integer offerId) {
        SimpleUser offer = simpleUserRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if user has created more than 4 carpools
        if (offer.getCarpolingDoneSuser() >= 4 && offer.getSubscription() == null) {
            throw new RuntimeException("You have reached the limit of 4 carpool offers. A subscription is required to create more offers.");
        }

        // Associate the user with the carpool
        carpool.setSimpleUserOffer(offer);

        // Save the carpool
        Carpool savedCarpool = carpoolRepository.save(carpool);

        // Increment carpoolingDoneSuser
        offer.setCarpolingDoneSuser(offer.getCarpolingDoneSuser() + 1);
        simpleUserRepository.save(offer);

        return savedCarpool;
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
        notificationService.sendCarpoolJoinNotification(carpool, user);
        userActivityService.addPointsForJoin(user);
        return carpool;
    }

    @Override
    public void deleteCarpool(Integer carpoolId, Integer offerId) {
        // Retrieve the carpool
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found"));

        // Verify that the user is the owner of the carpool
        if (!carpool.getSimpleUserOffer().getUserId().equals(offerId)) {
            throw new RuntimeException("You are not authorized to delete this carpool");
        }

        // Get the user who created the carpool
        SimpleUser offer = carpool.getSimpleUserOffer();

        // Fetch joined users before deletion
        Set<SimpleUser> joinedUsers = carpool.getSimpleUserJoin();
        System.out.println("Joined users for carpool " + carpoolId + ": " + joinedUsers.size() + " users"); // Debug
        joinedUsers.forEach(user -> System.out.println("Joined user ID: " + user.getUserId())); // Debug

        // Delete the carpool
        carpoolRepository.delete(carpool);

        // Decrement carpoolingDoneSuser, ensuring it doesn't go below 0
        if (offer.getCarpolingDoneSuser() > 0) {
            offer.setCarpolingDoneSuser(offer.getCarpolingDoneSuser() - 1);
            simpleUserRepository.save(offer);
        }

        // Send notifications if there are joined users
        if (!joinedUsers.isEmpty()) {
            System.out.println("Sending CARPOOL_DELETED notifications for carpool " + carpoolId); // Debug
            notificationService.sendCarpoolDeletedNotification(carpool, joinedUsers);
        } else {
            System.out.println("No joined users for carpool " + carpoolId + ", no notifications sent"); // Debug
        }
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

        notificationService.sendCarpoolLeaveNotification(carpool, user);

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
                    .forEach(carpool -> {
                        allRecommended.add(carpool);
                        notificationService.sendRecommendedCarpoolNotification(user, carpool);
                        log.info("Sent recommended carpool notification for userId: {}, carpoolId: {}", userId, carpool.getCarpoolId());
                    });
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








    @Override
    public Carpool rateCarpoolOfferer(Integer carpoolId, Integer userId, Boolean liked) {
        // Récupérer le covoiturage
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));

        // Récupérer l'utilisateur
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        SimpleUser offerer = carpool.getSimpleUserOffer();

        // Vérifications
        if (!carpool.getSimpleUserJoin().contains(user)) {
            throw new RuntimeException("You can only rate a carpool you joined!");
        }
        if (liked == null) {
            throw new RuntimeException("Please specify if you liked the carpool!");
        }
        Map<Integer, Boolean> ratings = getRatings(carpool);
        if (ratings.containsKey(userId)) {
            throw new RuntimeException("You have already rated this carpool!");
        }
        LocalDate carpoolDate = carpool.getCarpoolDate();
        LocalTime carpoolTime = carpool.getCarpoolTime();
        if (!carpoolDate.atTime(carpoolTime).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot rate a carpool that has not yet occurred!");
        }

        // Sauvegarder l'état initial pour rollback manuel si nécessaire
        String originalRatings = carpool.getRatings();

        // Ajouter la notation
        ratings.put(userId, liked);
        try {
            setRatings(carpool, ratings);
            carpoolRepository.save(carpool);
        } catch (Exception e) {
            // Rollback manuel
            carpool.setRatings(originalRatings);
            log.error("Failed to save carpool ratings", e);
            throw new RuntimeException("Error saving carpool rating");
        }

        // Mettre à jour la moyenne de l'offreur
        try {
            calculateOffererAverageRating(offerer.getUserId());
        } catch (Exception e) {
            // Rollback manuel : supprimer la notation ajoutée
            ratings.remove(userId);
            setRatings(carpool, ratings);
            carpoolRepository.save(carpool);
            log.error("Failed to update offerer rating", e);
            throw new RuntimeException("Error updating offerer rating");
        }

        log.info("User {} rated carpool {} with liked={}. Offerer {} rating updated.", userId, carpoolId, liked, offerer.getUserId());
        return carpool;
    }

    @Override
    public void calculateOffererAverageRating(Integer offererId) {
        SimpleUser offerer = simpleUserRepository.findById(offererId)
                .orElseThrow(() -> new RuntimeException("Offerer not found!"));
        List<Carpool> offeredCarpools = carpoolRepository.findBySimpleUserOffer_UserId(offererId);

        long totalRatings = 0;
        long positiveRatings = 0;

        for (Carpool carpool : offeredCarpools) {
            Map<Integer, Boolean> ratings = getRatings(carpool);
            totalRatings += ratings.size();
            positiveRatings += ratings.values().stream().filter(Boolean::booleanValue).count();
        }

        Double previousAverageRating = offerer.getAverageRating();

        try {
            if (totalRatings > 0) {
                double average = (double) positiveRatings / totalRatings * 100; // Pourcentage de "Oui"
                offerer.setAverageRating(average);
            } else {
                offerer.setAverageRating(null); // Pas de notes
            }
            simpleUserRepository.save(offerer);
        } catch (Exception e) {
            // Rollback manuel
            offerer.setAverageRating(previousAverageRating);
            simpleUserRepository.save(offerer);
            log.error("Failed to save offerer average rating", e);
            throw new RuntimeException("Error saving offerer average rating");
        }
    }

    @Override
    public String getOffererRating(Integer offererId) {
        SimpleUser offerer = simpleUserRepository.findById(offererId)
                .orElseThrow(() -> new RuntimeException("Offerer not found!"));
        Double averageRating = offerer.getAverageRating();
        if (averageRating == null) {
            return "Not Rated";
        }
        return String.format("%.0f%%", averageRating);
    }

    @Override
    public List<Map<Integer, Boolean>> getCarpoolRatings(Integer carpoolId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));
        Map<Integer, Boolean> ratings = getRatings(carpool);
        List<Map<Integer, Boolean>> result = new ArrayList<>();
        ratings.forEach((userId, liked) -> {
            Map<Integer, Boolean> rating = new HashMap<>();
            rating.put(userId, liked);
            result.add(rating);
        });
        return result;
    }

    private Map<Integer, Boolean>getRatings(Carpool carpool) {
        try {
            if (carpool.getRatings() == null || carpool.getRatings().isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(carpool.getRatings(), new TypeReference<Map<Integer, Boolean>>() {});
        } catch (Exception e) {
            log.error("Failed to parse ratings", e);
            throw new RuntimeException("Error parsing ratings");
        }
    }

    private void setRatings(Carpool carpool, Map<Integer, Boolean> ratings) {
        try {
            carpool.setRatings(ratings.isEmpty() ? null : objectMapper.writeValueAsString(ratings));
        } catch (Exception e) {
            log.error("Failed to serialize ratings", e);
            throw new RuntimeException("Error serializing ratings");
        }
    }



    //dashboard
    @Override
    public long getTotalCarpools() {
        System.out.println("Fetching total number of carpools");
        return carpoolRepository.count();
    }


    @Override
    public List<Map<String, Object>> getTopRatedOfferers(int limit) {
        System.out.println("Fetching top " + limit + " rated carpool offerers");
        List<SimpleUser> offerers = simpleUserRepository.findByAverageRatingIsNotNull();
        if (offerers.isEmpty()) {
            System.out.println("No users with average ratings found");
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = offerers.stream()
                .filter(user -> !carpoolRepository.findBySimpleUserOffer_UserId(user.getUserId()).isEmpty())
                .sorted(Comparator.comparing(SimpleUser::getAverageRating, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .map(user -> {
                    Map<String, Object> offerer = new HashMap<>();
                    offerer.put("userId", user.getUserId());
                    offerer.put("firstName", user.getUserFirstName());
                    offerer.put("lastName", user.getUserLastName());
                    offerer.put("name", user.getUserFirstName() + " " + user.getUserLastName()); // Concatenated name
                    offerer.put("averageRating", user.getAverageRating());
                    offerer.put("profilePhoto", user.getUserProfilePhoto() != null ? user.getUserProfilePhoto() : "default.jpg");
                    return offerer;
                })
                .collect(Collectors.toList());
        System.out.println("Found " + result.size() + " top rated offerers");
        return result;
    }


}