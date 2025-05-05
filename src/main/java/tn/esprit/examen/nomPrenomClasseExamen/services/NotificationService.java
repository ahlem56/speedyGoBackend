package tn.esprit.examen.nomPrenomClasseExamen.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.controllers.NotificationController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.NotificationRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;

import java.time.Instant;
import java.util.*;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationController notificationController;
    private final SimpleUserRepository simpleUserRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, @Lazy NotificationController notificationController, SimpleUserService simpleUserService, SimpleUserRepository simpleUserRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationController = notificationController;
        this.simpleUserRepository =simpleUserRepository;
    }

    // Save new notification
    public Notification createNotification(String content, SimpleUser simpleUser) {
        if (content.length() > 1000) {
            content = content.substring(0, 1000);  // Truncate the content if it's too long
        }
        Notification notification = new Notification();
        notification.setNotificationContent(content);
        notification.setNotificationDate(new Date());
        notification.setNotificationStatus("PENDING");
        notification.setSimpleUser(simpleUser);
        return notificationRepository.save(notification);
    }

    // Send notification to a specific user (passenger)
    public void sendNotificationToUser(String message, SimpleUser simpleUser) {
        // Create the notification in the database
        Notification notification = createNotification(message, simpleUser);
        // Send the notification to the specific user via SSE (passenger's userId)
        notificationController.sendNotificationToUser(message, simpleUser.getUserId());
    }

    // Method to send trip acceptance notification to the passenger
    public void sendTripAcceptanceNotification(Trip trip) {
        SimpleUser passenger = trip.getSimpleUser();
        Map<String, Object> message = new HashMap<>();
        message.put("type", "TRIP_ACCEPTED");
        message.put("message", String.format(
                "Your trip from %s to %s has been confirmed!",
                trip.getTripDeparture(),
                trip.getTripDestination()
        ));
        message.put("details", Map.of(
                "departure", trip.getTripDeparture(),
                "destination", trip.getTripDestination(),
                "date", trip.getTripDate().toString(),
                "price", trip.getTripPrice(),
                "passengers", trip.getNumberOfPassengers()
        ));
        message.put("timestamp", new Date());

        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            createNotification(jsonMessage, passenger); // Save notification to DB
            notificationController.sendNotificationToUser(jsonMessage, passenger.getUserId());  // Send notification only to the passenger
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Method to send trip refusal notification to the passenger
    public void sendTripRefusalNotification(Trip trip) {
        SimpleUser passenger = trip.getSimpleUser();
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("refused", true); // Ensure this is correctly set for refusals
        messageData.put("departure", trip.getTripDeparture());
        messageData.put("destination", trip.getTripDestination());

        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(messageData);
            createNotification(jsonMessage, passenger);  // Save to DB
            notificationController.sendNotificationToUser(jsonMessage, passenger.getUserId());  // Send to specific passenger
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Method to send event creation notifications to all users (this is the only scenario where all users receive notifications)
    public void sendEventCreationNotification(Event event) {
        // 1. Get all users who should receive this notification
        List<SimpleUser> allUsers = simpleUserRepository.findAll(); // Or use a more targeted query

        // 2. Create and save notification for each user
        for (SimpleUser user : allUsers) {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("eventDescription", event.getEventDescription());
            messageData.put("eventDate", event.getEventDate().toString());
            messageData.put("eventLocation", event.getEventLocation());
            messageData.put("timestamp", new Date());

            try {
                String jsonMessage = new ObjectMapper().writeValueAsString(messageData);
                createNotification(jsonMessage, user); // Save to DB for each user
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // 3. Broadcast to all connected clients
        notificationController.sendNotificationToClients(
                String.format(
                        "{\"eventDescription\": \"%s\", \"eventDate\": \"%s\", \"eventLocation\": \"%s\"}",
                        event.getEventDescription(),
                        event.getEventDate(),
                        event.getEventLocation()
                )
        );
    }
    //  Envoie une notification quand un colis est expédié
    public void sendParcelShippedNotification(Parcel parcel) {
        SimpleUser user = parcel.getSimpleUser(); // 🔁 Assure-toi que getCreatedBy() renvoie bien un SimpleUser

        Map<String, Object> message = new HashMap<>();
        message.put("type", "PARCEL_SHIPPED");
        message.put("message", String.format(
                "Your parcel to %s has been shipped!",
                parcel.getParcelDestination()
        ));
        message.put("parcelId", parcel.getParcelId());  // <--- Add this
        message.put("parcelStatus", parcel.getStatus().toString()); // <--- Add this (important!)
        message.put("details", Map.of(
                "parcelId", parcel.getParcelId(),
                "destination", parcel.getParcelDestination(),
                "status", parcel.getStatus().toString()
        ));
        message.put("timestamp", new Date());

        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            createNotification(jsonMessage, user);
            notificationController.sendNotificationToUser(jsonMessage, user.getUserId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }












    // Send notification to carpool offerer when a user joins
    public void sendCarpoolJoinNotification(Carpool carpool, SimpleUser joiningUser) {
        SimpleUser offerer = carpool.getSimpleUserOffer();
        Map<String, Object> message = new HashMap<>();
        message.put("type", "CARPOOL_JOINED");
        message.put("message", String.format(
                "%s %s has joined your carpool from %s to %s!",
                joiningUser.getUserFirstName(),
                joiningUser.getUserLastName(),
                carpool.getCarpoolDeparture(),
                carpool.getCarpoolDestination()
        ));
        message.put("details", Map.of(
                "carpoolId", carpool.getCarpoolId(),
                "departure", carpool.getCarpoolDeparture(),
                "destination", carpool.getCarpoolDestination(),
                "date", carpool.getCarpoolDate().toString(),
                "time", carpool.getCarpoolTime().toString(),
                "userId", joiningUser.getUserId()
        ));
        message.put("timestamp", new Date());

        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            createNotification(jsonMessage, offerer);
            notificationController.sendNotificationToUser(jsonMessage, offerer.getUserId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Send notification to carpool offerer when a user leaves (cancels)
    public void sendCarpoolLeaveNotification(Carpool carpool, SimpleUser leavingUser) {
        SimpleUser offerer = carpool.getSimpleUserOffer();
        Map<String, Object> message = new HashMap<>();
        message.put("type", "CARPOOL_LEFT");
        message.put("message", String.format(
                "%s %s has left your carpool from %s to %s.",
                leavingUser.getUserFirstName(),
                leavingUser.getUserLastName(),
                carpool.getCarpoolDeparture(),
                carpool.getCarpoolDestination()
        ));
        message.put("details", Map.of(
                "carpoolId", carpool.getCarpoolId(),
                "departure", carpool.getCarpoolDeparture(),
                "destination", carpool.getCarpoolDestination(),
                "date", carpool.getCarpoolDate().toString(),
                "time", carpool.getCarpoolTime().toString(),
                "userId", leavingUser.getUserId()
        ));
        message.put("timestamp", new Date());

        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            createNotification(jsonMessage, offerer);
            notificationController.sendNotificationToUser(jsonMessage, offerer.getUserId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Send notification to joined users when a carpool is deleted
    public void sendCarpoolDeletedNotification(Carpool carpool, Set<SimpleUser> joinedUsers) {
        for (SimpleUser user : joinedUsers) {
            System.out.println("Preparing CARPOOL_DELETED notification for user " + user.getUserId()); // Debug
            Map<String, Object> message = new HashMap<>();
            message.put("type", "CARPOOL_DELETED");
            message.put("message", String.format(
                    "The carpool from %s to %s has been deleted by the offerer.",
                    carpool.getCarpoolDeparture(),
                    carpool.getCarpoolDestination()
            ));
            message.put("details", Map.of(
                    "carpoolId", carpool.getCarpoolId(),
                    "departure", carpool.getCarpoolDeparture(),
                    "destination", carpool.getCarpoolDestination(),
                    "date", carpool.getCarpoolDate().toString(),
                    "time", carpool.getCarpoolTime().toString()
            ));
            message.put("timestamp", Instant.now().toString()); // Use ISO string for consistency

            try {
                String jsonMessage = new ObjectMapper().writeValueAsString(message);
                System.out.println("Sending CARPOOL_DELETED to user " + user.getUserId() + ": " + jsonMessage); // Debug
                createNotification(jsonMessage, user);
                notificationController.sendNotificationToUser(jsonMessage, user.getUserId());
            } catch (JsonProcessingException e) {
                System.err.println("Error serializing CARPOOL_DELETED for user " + user.getUserId() + ": " + e.getMessage()); // Debug
                e.printStackTrace();
            }
        }
    }

    // Send notification to user about a recommended carpool
    public void sendRecommendedCarpoolNotification(SimpleUser user, Carpool carpool) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "CARPOOL_RECOMMENDED");
        message.put("message", String.format(
                "We found a recommended carpool for you from %s to %s!",
                carpool.getCarpoolDeparture(),
                carpool.getCarpoolDestination()
        ));
        message.put("details", Map.of(
                "carpoolId", carpool.getCarpoolId(),
                "departure", carpool.getCarpoolDeparture(),
                "destination", carpool.getCarpoolDestination(),
                "date", carpool.getCarpoolDate().toString(),
                "time", carpool.getCarpoolTime().toString(),
                "price", carpool.getCarpoolPrice(),
                "capacity", carpool.getCarpoolCapacity()
        ));
        message.put("timestamp", new Date());

        try {
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            createNotification(jsonMessage, user);
            notificationController.sendNotificationToUser(jsonMessage, user.getUserId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public List<Notification> getAllNotificationsForUser(SimpleUser user) {
        return notificationRepository.findBySimpleUserOrderByNotificationDateDesc(user);
    }
}