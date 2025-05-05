package tn.esprit.examen.nomPrenomClasseExamen.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommissionDTO {
    private Integer commissionId;
    private Integer partnerId;
    private Integer paymentId;
    private BigDecimal amount;
    private Boolean paidOut;
    private String description;
    private LocalDateTime calculatedAt;
    private LocalDateTime updatedAt;
    private String partnerName;
}