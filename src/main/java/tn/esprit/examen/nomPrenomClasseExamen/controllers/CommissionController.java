package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Commission;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.CommissionRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.CommissionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/commissions")
public class CommissionController {
    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private CommissionService commissionService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<tn.esprit.examen.nomPrenomClasseExamen.dto.CommissionDTO> getAllCommissions() {
        return commissionService.getAllCommissions();
    }

    @GetMapping("/partner/{partnerId}")
    @PreAuthorize("hasRole('SIMPLE_USER') or hasRole('ADMIN')")
    public List<tn.esprit.examen.nomPrenomClasseExamen.dto.CommissionDTO> getCommissionsByPartner(@PathVariable Integer partnerId) {
        return commissionService.getCommissionsForPartner(partnerId);
    }

    @GetMapping("/partner/{partnerId}/summary")
    @PreAuthorize("hasRole('SIMPLE_USER') or hasRole('ADMIN')")
    public Map<String, BigDecimal> getCommissionSummary(@PathVariable Integer partnerId) {
        return commissionService.getCommissionSummary(partnerId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Commission createCommission(@RequestBody Commission commission) {
        return commissionService.createCommission(
                commission.getPartnerId(),
                commission.getAmount(),
                commission.getDescription()
        );
    }

    @GetMapping("/{commissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public tn.esprit.examen.nomPrenomClasseExamen.dto.CommissionDTO getCommissionDetails(@PathVariable Integer commissionId) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new IllegalArgumentException("Commission not found"));
        return commissionService.toDTO(commission);
    }

    @PatchMapping("/{commissionId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Commission updateCommissionStatus(@PathVariable Integer commissionId, @RequestBody Map<String, Boolean> request) {
        Commission commission = commissionRepository.findById(commissionId)
                .orElseThrow(() -> new IllegalArgumentException("Commission not found"));
        commission.setPaidOut(request.get("paidOut"));
        commission.setUpdatedAt(LocalDateTime.now());
        return commissionRepository.save(commission);
    }
}