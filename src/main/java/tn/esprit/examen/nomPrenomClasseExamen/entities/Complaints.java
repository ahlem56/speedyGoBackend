package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Complaints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer complaintId;
    @NonNull
    private String complaintDescription;
    @NonNull
    private Date complaintCreationDate;
    @Enumerated(EnumType.STRING)
    private ComplaintStatus complaintStatus = ComplaintStatus.pending;
    private String Response;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "simple_user_user_id")
    private SimpleUser simpleUser;

}
