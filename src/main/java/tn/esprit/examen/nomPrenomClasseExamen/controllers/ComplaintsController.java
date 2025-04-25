package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Complaints;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.services.ComplaintsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/complaints")
@AllArgsConstructor
public class ComplaintsController {

    private final ComplaintsService complaintsService;

    @PostMapping("/add/{simpleUserId}")
    public Complaints addComplaint(@RequestBody Complaints complaint, @PathVariable Integer simpleUserId) {
        return complaintsService.createComplaint(complaint, simpleUserId);
    }

    @PutMapping("/update/{complaintId}/{simpleUserId}")
    public Complaints updateComplaint(@PathVariable Integer complaintId, @PathVariable Integer simpleUserId, @RequestBody Complaints updatedComplaint) {
        return complaintsService.updateComplaint(complaintId, updatedComplaint, simpleUserId);
    }

    @DeleteMapping("/delete/{complaintId}/{simpleUserId}")
    public void deleteComplaint(@PathVariable Integer complaintId, @PathVariable Integer simpleUserId) {
        complaintsService.deleteComplaint(complaintId, simpleUserId);
    }

    @GetMapping("/user/{simpleUserId}")
    public List<Complaints> getComplaintsByUser(@PathVariable Integer simpleUserId) {
        return complaintsService.getComplaintsByUser(simpleUserId);
    }

    @PostMapping("/respond/{complaintId}/{adminId}")
    public ResponseEntity<Complaints> respondToComplaint(@PathVariable Integer complaintId,
                                                         @PathVariable Integer adminId,
                                                         @RequestBody Map<String, String> request) {
        String response = request.get("response");

        Complaints updatedComplaint = complaintsService.respondToComplaint(complaintId, response, adminId);

        return ResponseEntity.ok(updatedComplaint);
    }

    @PutMapping("/{id}/ignore/{adminId}")
    public ResponseEntity<Complaints> ignoreComplaint(@PathVariable Integer id, @PathVariable Integer adminId) {
        Complaints ignoredComplaint = complaintsService.ignoreComplaint(id, adminId);
        return ResponseEntity.ok(ignoredComplaint);
    }



    @GetMapping("/")
    public List<Complaints> getAllComplaints() {
        return complaintsService.getAllComplaints();
    }

    @GetMapping("/{complaintId}")
    public ResponseEntity<Complaints> getComplaintById(@PathVariable Integer complaintId) {
        return complaintsService.getComplaintById(complaintId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/{complaintId}/user")
    public ResponseEntity<SimpleUser> getUserByComplaintId(@PathVariable Integer complaintId) {
        SimpleUser user = complaintsService.getSimpleUserByComplaintId(complaintId);
        return ResponseEntity.ok(user);
    }

}
