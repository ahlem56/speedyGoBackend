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
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehiculeId;
    private VehicleType vehicleType;
    private String vehicleModel;
    private Integer vehicleCapacity;
    private Integer vehicleSerialNumber;
    private Date vehiculemMintenanceDate;
    private Boolean vehiculeInsuranceStatus;
    private Date vehiculeInsuranceDate;

    @OneToOne (mappedBy = "vehicle")
    private Driver driver;


}