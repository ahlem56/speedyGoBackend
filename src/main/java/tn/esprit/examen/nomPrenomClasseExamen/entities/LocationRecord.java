package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRecord {
    private Double latitude;
    private Double longitude;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
}
