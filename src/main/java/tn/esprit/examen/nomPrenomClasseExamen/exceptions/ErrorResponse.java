package tn.esprit.examen.nomPrenomClasseExamen.exceptions;

import lombok.Getter;
import lombok.Setter;

public class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}