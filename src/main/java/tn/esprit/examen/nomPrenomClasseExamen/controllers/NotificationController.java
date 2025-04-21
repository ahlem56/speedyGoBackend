package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Notification;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.NotificationService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/notifications")
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpleUserRepository simpleUserRepository;

    private static final int MAX_EMITTERS = 100;

    // Mapping from userId to their corresponding SseEmitter
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    // Constructor injection (recommended approach)
    public NotificationController(NotificationService notificationService,
                                  SimpleUserRepository simpleUserRepository) {
        this.notificationService = notificationService;
        this.simpleUserRepository = simpleUserRepository;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@RequestParam Integer userId) {
        if (emitters.size() >= MAX_EMITTERS) {
            throw new IllegalStateException("Max emitter connections reached.");
        }

        SseEmitter emitter = new SseEmitter(0L); // Keep the connection open indefinitely
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        try {
            emitter.send("Connected to SSE stream.");
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    // Send notification to a specific user based on their userId
    public void sendNotificationToUser(String message, Integer userId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(message);
            } catch (Exception e) {
                emitters.remove(userId);
            }
        }
    }

    // Send notification to all connected clients (e.g., for event creation notifications)
    public void sendNotificationToClients(String message) {
        for (SseEmitter emitter : emitters.values()) {
            try {
                emitter.send(message);
            } catch (Exception e) {
                // Clean up failed connections
                emitters.values().remove(emitter);
            }
        }
    }

    @GetMapping("/all")
    public List<Notification> getAllNotifications(@RequestParam Integer userId) {
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return notificationService.getAllNotificationsForUser(user);
    }
}