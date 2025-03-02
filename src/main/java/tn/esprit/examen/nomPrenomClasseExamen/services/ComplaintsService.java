package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ComplaintStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Complaints;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.AdminRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ComplaintsRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ComplaintsService implements IComplaintsService {

    private final ComplaintsRepository complaintsRepository;
    private final SimpleUserRepository simpleUserRepository;
    private final AdminRepository adminRepository;

    @Override
    public Complaints createComplaint(Complaints complaint, Integer simpleUserId) {
        SimpleUser simpleUser = simpleUserRepository.findById(simpleUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        complaint.setSimpleUser(simpleUser);
        complaint.setComplaintCreationDate(new java.util.Date());
        complaint.setComplaintStatus(ComplaintStatus.pending);
        return complaintsRepository.save(complaint);
    }

    @Override
    public Complaints updateComplaint(Integer id, Complaints updatedComplaint, Integer simpleUserId) {
        Optional<Complaints> existingComplaint = complaintsRepository.findById(id);

        if (existingComplaint.isPresent() &&
                existingComplaint.get().getSimpleUser().getUserId().equals(simpleUserId) &&
                existingComplaint.get().getComplaintStatus() == ComplaintStatus.pending) {

            Complaints complaint = existingComplaint.get();
            complaint.setComplaintDescription(updatedComplaint.getComplaintDescription());
            return complaintsRepository.save(complaint);
        }

        throw new RuntimeException("Modification non autorisée ou réclamation déjà traitée");
    }

    @Override
    public void deleteComplaint(Integer id, Integer simpleUserId) {
        Optional<Complaints> existingComplaint = complaintsRepository.findById(id);

        if (existingComplaint.isPresent() &&
                existingComplaint.get().getSimpleUser().getUserId().equals(simpleUserId) &&
                existingComplaint.get().getComplaintStatus() == ComplaintStatus.pending) {

            complaintsRepository.delete(existingComplaint.get());
            return;
        }

        throw new RuntimeException("Suppression non autorisée ou réclamation déjà traitée");
    }

    @Override
    public List<Complaints> getComplaintsByUser(Integer simpleUserId) {
        SimpleUser simpleUser = simpleUserRepository.findById(simpleUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return complaintsRepository.findBySimpleUser(simpleUser);
    }

    @Override
    public Complaints respondToComplaint(Integer id, String response, Integer adminId) {
        Complaints complaint = complaintsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée"));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin non trouvé"));

        complaint.setResponse(response);
        complaint.setComplaintStatus(ComplaintStatus.resolved);

        return complaintsRepository.save(complaint);
    }

    @Override
    public Complaints ignoreComplaint(Integer id, Integer adminId) {
        Complaints complaint = complaintsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée"));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin non trouvé"));

        complaint.setComplaintStatus(ComplaintStatus.ignored);

        return complaintsRepository.save(complaint);
    }


    @Override
    public List<Complaints> getAllComplaints() {
        return complaintsRepository.findAllWithUsers();
    }

    @Override
    public Optional<Complaints> getComplaintById(Integer complaintId) {
        return complaintsRepository.findById(complaintId);
    }
}
