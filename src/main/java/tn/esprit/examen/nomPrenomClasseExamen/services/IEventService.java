package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Event;

import java.util.List;

public interface IEventService {
    List<Event> getAllEvents();

    Event getEventById(Integer idEvent);

    Event createEvent(Event event);

    Event updateEvent(Integer idEvent, Event eventDetails);

    void deleteEvent(Integer idEvent);

    void registerUser(Integer idEvent, Integer userId);

    void unregisterUser(Integer idEvent, Integer userId);
}
