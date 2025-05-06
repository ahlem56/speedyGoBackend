package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Partners {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer partnerId;
    private String partnerName;
    private String partnerContactInfo;
    private Integer partnerCode;
    private Integer partnershipDuration;

    @OneToMany(mappedBy = "partners", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<SimpleUser> simpleUsers = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "promotions_promotion_id")
    @JsonIgnoreProperties("partnerses") // Ignore the partnerses field in Promotions to break the cycle
    private Promotions promotions;
    @Column(precision = 10, scale = 2)
    private BigDecimal commissionRate = BigDecimal.valueOf(0.15); // 15% default

    @Column(precision = 12, scale = 2)
    private BigDecimal totalCommission = BigDecimal.ZERO;


}

