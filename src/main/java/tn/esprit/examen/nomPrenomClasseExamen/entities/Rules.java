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

    private Integer pointsForJoin; // Points added when a user joins a carpool
    private Integer pointsForCancel; // Points deducted when a user cancels a carpool
    private Integer pointsDeductedPerInactiveDay; // Points deducted per day of inactivity
    private Integer inactiveDaysThreshold; // Number of days without activity before deduction
    private Integer topActivePointsThreshold; // Points required for TOP_ACTIVE level
    private Integer contributeurPointsThreshold; // Points required for CONTRIBUTEUR level


    @ManyToOne
    @JoinColumn(name = "trip_trip_id")
    private Trip trip;

}
