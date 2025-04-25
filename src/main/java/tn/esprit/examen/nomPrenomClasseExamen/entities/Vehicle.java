package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehiculeId;
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    private String vehicleModel;
    private Integer vehicleCapacity;
    private Integer vehicleSerialNumber;
    private Date vehiculeMaintenanceDate;
    private Boolean vehiculeInsuranceStatus;
    private Date vehiculeInsuranceDate;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @JsonBackReference  // Ensure this annotation is present in Vehicle
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Driver driver;

}