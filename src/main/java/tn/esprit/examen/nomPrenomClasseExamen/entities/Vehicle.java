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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private Double latitude;
    private Double longitude;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ElementCollection
    @CollectionTable(name = "vehicle_location_history", joinColumns = @JoinColumn(name = "vehicle_id"))
    private List<LocationRecord> travelHistory = new ArrayList<>();

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Driver driver;

}