package tn.esprit.examen.nomPrenomClasseExamen.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfile {
    private Integer userId;
    private String email;
    private PartnerDTO partner;
}
