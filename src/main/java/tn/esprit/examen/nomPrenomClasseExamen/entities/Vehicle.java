package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @NotNull(message = "Vehicule type is required")
    private VehicleType vehicleType;

    @NotNull(message = "Vehicle model must not be null")
    @Size(min = 2, max = 50, message = "Vehicle model must be between 2 and 50 characters")
    private String vehicleModel;

    @NotNull(message = "Vehicle capacity must be provided")
    @Min(value = 1, message = "Vehicle capacity must be at least 1")
    @Max(value = 4,message = "Maximum Vehicle capacity is 4")
    private Integer vehicleCapacity;
    @NotNull(message = "Vehicle serial number is required")
    private Integer vehicleSerialNumber;
    @PastOrPresent(message = "Maintenance date must be in the past or present")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vehiculeMaintenanceDate;
    @NotNull(message = "Insurance status must be specified")
    private Boolean vehiculeInsuranceStatus;
    @Temporal(TemporalType.TIMESTAMP)
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


