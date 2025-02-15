package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ruleId;
    private Float maxWeight;         // Max weight allowed for the trip
    private Float costPerKm;         // Cost per kilometer traveled
    private Integer maxPassengers;   // Max number of passengers allowed
    private String allowedLuggage;  // Type of luggage allowed (e.g., 'hand luggage only')
    private String timeRestrictions; // Time restrictions (e.g., 'no trips before 9 AM')
    private Integer minDriverExperience; // Minimum years of experience required for the driver
    private Integer minAge;          // Minimum age for the passenger or driver
    private String vehicleTypeRestrictions; // Restrictions on vehicle types (e.g., 'only SUVs')


    @ManyToOne
    @JoinColumn(name = "trip_trip_id")
    private Trip trip;


}
