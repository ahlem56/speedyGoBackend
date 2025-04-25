package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Integer payementId;
    private Date paymentDate;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private Float paymentAmount;
    private String stripeChargeId;
    @OneToOne
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;

    @OneToOne(mappedBy = "payment")
    private Trip trip;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;


}
