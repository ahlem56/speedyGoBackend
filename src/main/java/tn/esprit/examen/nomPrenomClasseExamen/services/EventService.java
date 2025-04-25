package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Event;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.EventRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class EventService implements IEventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SimpleUserRepository simpleUserRepository;
    @Autowired
    private NotificationService notificationService;  // Inject NotificationService

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Integer idEvent) {
        return eventRepository.findById(idEvent).orElse(null);
    }

    @Override
    public Event createEvent(Event event) {
        // Create the event and trigger notification
        Event createdEvent = eventRepository.save(event);

        // Send notifications about the new event to all users
        notificationService.sendEventCreationNotification(createdEvent);

        return createdEvent;
    }

    @Override
    public Event updateEvent(Integer idEvent, Event eventDetails) {
        Event existing = getEventById(idEvent);
        if (existing != null) {
            existing.setEventDate(eventDetails.getEventDate());
            existing.setEventDescription(eventDetails.getEventDescription());
            existing.setEventLocation(eventDetails.getEventLocation());
            // etc. for other fields
            return eventRepository.save(existing);
        }
        return null;
    }

    @Override
    public void deleteEvent(Integer idEvent) {
        eventRepository.deleteById(idEvent);
    }

    @Override
    public void registerUser(Integer idEvent, Integer userId) {
        Event event = getEventById(idEvent);
        SimpleUser user = simpleUserRepository.findById(userId).orElse(null);
        if (event != null && user != null) {
            event.getSimpleUsers().add(user);
            eventRepository.save(event);

            // Notify the user that they've been registered for the event
            String message = "You have been successfully registered for the event: " + event.getEventDescription();
            notificationService.sendNotificationToUser(message, user);
        }
    }

    @Override
    public void unregisterUser(Integer idEvent, Integer userId) {
        Event event = getEventById(idEvent);
        SimpleUser user = simpleUserRepository.findById(userId).orElse(null);
        if (event != null && user != null) {
            event.getSimpleUsers().remove(user);
            eventRepository.save(event);

            // Notify the user that they've been unregistered from the event
            String message = "You have been unregistered from the event: " + event.getEventDescription();
            notificationService.sendNotificationToUser(message, user);
        }
    }

    public List<SimpleUser> getAllSimpleUsers() {
        return simpleUserRepository.findAll();  // Assuming you have a SimpleUserRepository
    }

}
