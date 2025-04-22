package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partners {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer partnerId;
    private String partnerName;
    private String partnerContactInfo;
    private Integer partnerCode;
    private Integer partnershipDuration;

    @OneToMany(mappedBy = "partners", orphanRemoval = true)
    private Set<SimpleUser> simpleUsers = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "promotions_promotion_id")
    private Promotions promotions;
    @Column(precision = 10, scale = 2)
    private BigDecimal commissionRate = BigDecimal.valueOf(0.15); // 15% default

    @Column(precision = 12, scale = 2)
    private BigDecimal totalCommission = BigDecimal.ZERO;

}

