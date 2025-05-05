package tn.esprit.examen.nomPrenomClasseExamen.entities;

import lombok.Data;

@Data
public class SeverityRequest {
    private String text;

    public SeverityRequest(String text) {
        this.text = text;
    }
}