package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends User {
    private String licenseNumberD;
    private String insuranceDetailsD;
    private Float performanceRatingD;
    private String scheduleD;
    private boolean availabilityD = false;  // Default should be false if not selected, but ensure it's set to true when provided

    @OneToMany(mappedBy = "driver", orphanRemoval = true)
    private Set<Chat> chats = new LinkedHashSet<>();

    @OneToMany(mappedBy = "driver", orphanRemoval = true)
    private Set<Notification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "driver", orphanRemoval = true)
    private Set<Trip> trips = new LinkedHashSet<>();

    @OneToMany(mappedBy = "driver", orphanRemoval = true)
    private Set<Parcel> parcels = new LinkedHashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JsonManagedReference  // Use this to ensure vehicle is serialized
    private Vehicle vehicle;

    public void setId(Integer driverId) {
    }
}

