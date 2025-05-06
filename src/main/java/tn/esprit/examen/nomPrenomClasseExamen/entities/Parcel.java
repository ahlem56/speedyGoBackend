package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
    @NotNull
    private Integer recepeientPhoneNumber;
    @NotNull
    private Integer senderPhoneNumber;
    @NotNull
    private String parcelDeparture;
    @NotNull
    private String parcelDestination;
    private Integer parcelWeight;
  @Temporal(TemporalType.DATE)  // Stocker uniquement la date (sans heure)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
  private Date parcelDate;
  private float parcelPrice;

    @ManyToOne
    @JoinColumn(name = "simple_user_user_id")
    @JsonBackReference(value = "simpleUser-parcels")
    private SimpleUser simpleUser;

    @ManyToOne
    @JoinColumn(name = "driver_user_id")
    private Driver driver;
    @OneToOne (mappedBy = "parcel")
    @JsonBackReference(value = "parcel-payment")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private Status status; // Si 'status' est un enum, comme 'DELIVERED'
    private boolean archived = false; // default false


  //Damaged Parcel
  private String damageImageUrl; // chemin de l'image stockée
  private String damageDescription; // texte éventuellement saisi
  private LocalDateTime damageReportedAt; // date du signalement
}

