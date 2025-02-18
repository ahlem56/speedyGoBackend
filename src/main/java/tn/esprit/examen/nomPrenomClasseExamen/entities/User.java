package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Inheritance (strategy  = InheritanceType.JOINED)
public abstract class  User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @JsonProperty("firstName")
    private String userFirstName;
    @JsonProperty("lastName")
    private String userLastName;
    @JsonProperty("email")
    private String userEmail;
    @JsonProperty("password")
    private String userPassword;
    private String userProfilePhoto;
    @JsonProperty("address")
    private String userAddress;
    @JsonProperty("cin")
    private Integer userCin;
    @JsonProperty("birthDate")
    private Date userBirthDate;

}
