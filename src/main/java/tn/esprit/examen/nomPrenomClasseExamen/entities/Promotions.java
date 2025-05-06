package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promotionId;
    private String promotionTitle;
    private String promotionDescription;
    private Float promotionDiscountPercentage;
    private Date promotionStartDate;
    private Date promotionEndDate;

    @OneToMany(mappedBy = "promotions", orphanRemoval = true)
    @JsonIgnoreProperties("promotions") // Ignore the promotions field in Partners to break the cycle
    private Set<Partners> partnerses = new LinkedHashSet<>();
}