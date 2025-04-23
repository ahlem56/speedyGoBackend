package tn.esprit.examen.nomPrenomClasseExamen.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.controllers.NotificationController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.NotificationRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationController notificationController;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, @Lazy NotificationController notificationController) {
        this.notificationRepository = notificationRepository;
        this.notificationController = notificationController;
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
        String message = String.format(
                "{\"eventDescription\": \"%s\", \"eventDate\": \"%s\", \"eventLocation\": \"%s\"}",
                event.getEventDescription(),
                event.getEventDate(),
                event.getEventLocation()
        );
        notificationController.sendNotificationToClients(message);  // This sends the message to all connected users
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
    message.put("details", Map.of(
      "parcelId", parcel.getParcelId(),
      "destination", parcel.getParcelDestination(),
      "status", parcel.getStatus().toString()
    ));
    message.put("timestamp", new Date());

    try {
      String jsonMessage = new ObjectMapper().writeValueAsString(message);
      createNotification(jsonMessage, user);
      notificationController.sendNotificationToClients(jsonMessage);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }


    public List<Notification> getAllNotificationsForUser(SimpleUser user) {
        return notificationRepository.findBySimpleUserOrderByNotificationDateDesc(user);
    }
}
