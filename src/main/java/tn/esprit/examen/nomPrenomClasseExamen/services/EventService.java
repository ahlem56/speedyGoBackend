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
import java.util.stream.Collectors;

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
    public Event updateEvent(Event event) {
         return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Integer idEvent) {
        eventRepository.deleteById(idEvent);
    }

    @Override
    public void registerUser(Integer idEvent, Integer userId) {
        Event event = getEventById(idEvent);
        SimpleUser user = simpleUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(event.getSimpleUsers().size() >= event.getMaxParticipants()) {
            throw new RuntimeException("Event is full");
        }

        if(!event.getSimpleUsers().contains(user)) {
            event.getSimpleUsers().add(user);
            eventRepository.save(event);
        }
    }

    @Override
    public void unregisterUser(Integer idEvent, Integer userId) {
        Event event = getEventById(idEvent);
        SimpleUser user = simpleUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(event.getSimpleUsers().contains(user)) {
            event.getSimpleUsers().remove(user);
            eventRepository.save(event);
        }
    }

    @Override
    public List<Event> getAllEventsForUser(Integer userId) {
        // fetch the user once (or you can skip, since you only need the ID)
        SimpleUser user = simpleUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return eventRepository.findAll()
                .stream()
                .peek(event -> {
                    // 1) how many participants right now
                    int size = event.getSimpleUsers().size();
                    event.setCurrentParticipants(size);

                    // 2) is this user in that set?
                    boolean isReg = event.getSimpleUsers()
                            .stream()
                            .anyMatch(u -> u.getUserId().equals(userId));
                    event.setRegistered(isReg);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Event getEventWithMostParticipants() {
        return eventRepository.findAll().stream()
                .max((e1, e2) -> Integer.compare(e1.getSimpleUsers().size(), e2.getSimpleUsers().size()))
                .orElseThrow(() -> new RuntimeException("No events found"));
    }

}
