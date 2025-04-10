package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Notification;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.services.NotificationService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RequestMapping("/notifications")
@RestController
public class NotificationController {

    private static final int MAX_EMITTERS = 100;


    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications() {
        if (emitters.size() >= MAX_EMITTERS) {
            throw new IllegalStateException("Max emitter connections reached.");
        }

        SseEmitter emitter = new SseEmitter(0L); // 0L means no timeout, connection is kept open indefinitely
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        // You can optionally send an initial message here if you want
        try {
            emitter.send("Connected to SSE stream.");
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }


    public void sendNotificationToClients(String message) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(message);
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }




}

