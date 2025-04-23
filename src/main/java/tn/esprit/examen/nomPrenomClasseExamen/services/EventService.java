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
    private NotificationService notificationService;

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Integer idEvent) {
        return eventRepository.findById(idEvent).get();
    }

    @Override public Event createEvent(Event event) { // Create the event and trigger notification
         Event createdEvent = eventRepository.save(event); // Send notifications about the new event to all users
        notificationService.sendEventCreationNotification(createdEvent);
        return createdEvent; }

    @Override
    public Event updateEvent(Integer idEvent, Event eventDetails) {
        Event existing = getEventById(idEvent);
        existing.setEventDate(eventDetails.getEventDate());
        existing.setEventDescription(eventDetails.getEventDescription());
        return eventRepository.save(existing);
    }

    @Override
    public void deleteEvent(Integer idEvent) {
        eventRepository.deleteById(idEvent);
    }

    @Override
    public void registerUser(Integer idEvent, Integer userId) {
        Event event = getEventById(idEvent);
        SimpleUser user = simpleUserRepository.findById(userId).get();
        eventRepository.save(event);
    }

    @Override
    public void unregisterUser(Integer idEvent, Integer userId) {
        Event event = getEventById(idEvent);
        SimpleUser user = simpleUserRepository.findById(userId).get();
    }
    public List<SimpleUser> getAllSimpleUsers() {
        return simpleUserRepository.findAll();  // Assuming you have a SimpleUserRepository
    }
}
