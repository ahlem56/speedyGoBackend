package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Complaints;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;

import java.util.List;
import java.util.Optional;

public interface IComplaintsService {
    Complaints createComplaint(Complaints complaint, Integer simpleUserId);

    Complaints updateComplaint(Integer id, Complaints updatedComplaint, Integer simpleUserId);

    void deleteComplaint(Integer id, Integer simpleUserId);

    List<Complaints> getComplaintsByUser(Integer simpleUserId);

    Complaints respondToComplaint(Integer id, String response, Integer adminId) ;

    List<Complaints> getAllComplaints();

    Complaints ignoreComplaint(Integer id, Integer adminId);

    Optional<Complaints> getComplaintById(Integer complaintId);
}
