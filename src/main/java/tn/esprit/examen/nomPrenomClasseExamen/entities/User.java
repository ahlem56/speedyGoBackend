package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Optional;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "chats", "notifications", "trips", "parcels"})
@Inheritance (strategy  = InheritanceType.JOINED)
public abstract class  User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @NotNull
    @JsonProperty("firstName")
    private String userFirstName;
    @NotNull
    @JsonProperty("lastName")
    private String userLastName;
    @NotNull
    @JsonProperty("email")
    private String userEmail;
    @NotNull
    @JsonProperty("password")
    private String userPassword;
    private String userProfilePhoto;
    @JsonProperty("address")
    private String userAddress;
    @JsonProperty("cin")
    private Integer userCin;
    @JsonProperty("birthDate")
    private Date userBirthDate;
    @Column(name = "reset_token")
    private String resetToken;
    @JsonProperty("emergencyContactEmail")
    private String emergencyContactEmail;
    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partners partner;

}
