package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payementId;
    private Date paymentDate;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private Float paymentAmount;
    @OneToOne
    private Parcel parcel;

    @OneToOne(mappedBy = "payment")
    private Trip trip;

}

