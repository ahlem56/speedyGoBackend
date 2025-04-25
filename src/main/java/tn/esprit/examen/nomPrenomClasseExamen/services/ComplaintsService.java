package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.examen.nomPrenomClasseExamen.entities.ComplaintStatus;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Complaints;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.AdminRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ComplaintsRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@AllArgsConstructor
public class ComplaintsService implements IComplaintsService {

    private static final Logger log = LoggerFactory.getLogger(ComplaintsService.class);

    private final ComplaintsRepository complaintsRepository;
    private final SimpleUserRepository simpleUserRepository;
    private final AdminRepository adminRepository;

    @Override
    public Complaints createComplaint(Complaints complaint, Integer simpleUserId) {
        SimpleUser simpleUser = simpleUserRepository.findById(simpleUserId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        complaint.setSimpleUser(simpleUser);
        complaint.setComplaintCreationDate(new Date());
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

    @Override
    public SimpleUser getSimpleUserByComplaintId(Integer complaintId) {
        return complaintsRepository.findSimpleUserByComplaintId(complaintId);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Tous les jours à minuit
    @Transactional
    public void closeOldPendingComplaints() {
        log.info("Starting closeOldPendingComplaints at {}", new Date());

        // Calculer la date seuil (30 jours avant aujourd'hui, en UTC)
        LocalDate thresholdLocalDate = LocalDate.now(ZoneId.of("UTC")).minusDays(30);
        Date thresholdDate = Date.from(thresholdLocalDate.atStartOfDay(ZoneId.of("UTC")).toInstant());

        log.info("Threshold date for closing complaints: {}", thresholdDate);

        // Vérifier toutes les réclamations pending pour débogage
        List<Complaints> allPendingComplaints = complaintsRepository.findByComplaintStatus(ComplaintStatus.pending);
        log.info("Total pending complaints found: {}", allPendingComplaints.size());
        for (Complaints complaint : allPendingComplaints) {
            log.info("Pending complaint ID {} has creation date: {}",
                    complaint.getComplaintId(), complaint.getComplaintCreationDate());
        }

        // Récupérer les réclamations pending de plus de 30 jours
        List<Complaints> oldPendingComplaints = complaintsRepository.findByComplaintStatusAndCreationDateBefore(
                ComplaintStatus.pending, thresholdDate);

        log.info("Found {} old pending complaints to close", oldPendingComplaints.size());

        // Traiter chaque réclamation
        for (Complaints complaint : oldPendingComplaints) {
            if (complaint.getComplaintCreationDate() != null) {
                log.info("Processing complaint ID {} with creation date {}",
                        complaint.getComplaintId(), complaint.getComplaintCreationDate());
                complaint.setComplaintStatus(ComplaintStatus.ignored);
                complaint.setResponse("Réclamation non traitée après 30 jours");
                complaintsRepository.save(complaint);
                log.info("Closed complaint ID {} as ignored", complaint.getComplaintId());
            } else {
                log.warn("Skipping complaint ID {} with null creation date", complaint.getComplaintId());
            }
        }

        log.info("Finished closeOldPendingComplaints");
    }
}