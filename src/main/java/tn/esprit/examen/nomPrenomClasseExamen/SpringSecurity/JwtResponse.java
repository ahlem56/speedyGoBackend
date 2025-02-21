package tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity;

public class JwtResponse {
  private String token;
  private String role; // Ajout du rôle
  // Constructeur
  public JwtResponse(String token, String role) {
    this.token = token;
    this.role = role;
  }
  // Getters
  public String getToken() {
    return token;
  }

  public String getRole() {
    return role;
  }
  // Setters
  public void setToken(String token) {
    this.token = token;
  }
  public void setRole(String role) {
    this.role = role;
  }
}
