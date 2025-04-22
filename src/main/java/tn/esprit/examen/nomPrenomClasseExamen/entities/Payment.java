package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;
    private Date paymentDate;
    @Enumerated(EnumType.STRING)
    @Column(length = 20) // 👈 ensures enough space for enum string
    private PaymentMethod paymentMethod;

    @Column(precision = 19, scale = 2) // Stores values up to 99999999999999999.99
    @NotNull
    private BigDecimal paymentAmount;
    private String stripeChargeId;
    @OneToOne
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;

    @OneToOne(mappedBy = "payment")
    private Trip trip;
    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partners partner;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name = "commission_calculated")
    private boolean commissionCalculated = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;


}
