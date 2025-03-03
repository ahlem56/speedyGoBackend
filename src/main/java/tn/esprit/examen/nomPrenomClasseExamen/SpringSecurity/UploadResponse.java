package tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity;

public class UploadResponse {
    private String message;

    // Constructor
    public UploadResponse(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }

    // Setter
    public void setMessage(String message) {
        this.message = message;
    }
}