package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Partners;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commissionId;

    private Integer partnerId;

    private Integer paymentId;

    private BigDecimal amount;

    private Boolean paidOut;

    private String description;

    private LocalDateTime calculatedAt;

    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "paymentId", insertable = false, updatable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "partnerId", insertable = false, updatable = false)
    private Partners partner;
    public Boolean isPaidOut() {
        return paidOut;
    }

}