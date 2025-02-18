package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;

public interface ITripService {
    public Trip createTrip(Trip trip, Integer simpleUserId, Integer driverId);
    public Trip updateTrip(Integer tripId, Trip updatedTrip);
    public void deleteTrip(Integer tripId);
    public Trip getTripById(Integer tripId);
}
