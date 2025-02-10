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

public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer parcelId;
    private ParcelCategory parcelCategory;
    private Integer recepeientPhoneNumber;
    private Integer senderPhoneNumber;
    private String parcelDeparture;
    private String parcelDestination;
    private Integer parcelWeight;
    private Date parcelDate;
    private float parcelPrice;

    @ManyToOne
    @JoinColumn(name = "simple_user_user_id")
    private SimpleUser simpleUser;

    @ManyToOne
    @JoinColumn(name = "driver_user_id")
    private Driver driver;
    @OneToOne (mappedBy = "parcel")
    private Payment payment;

}

