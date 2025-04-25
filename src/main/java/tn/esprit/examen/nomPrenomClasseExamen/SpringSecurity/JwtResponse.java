package tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity;

import tn.esprit.examen.nomPrenomClasseExamen.entities.User;

public class JwtResponse {
  private String token;
  private String role;
  private User user;  // Add a user field to store the full user object

  // Constructor
  public JwtResponse(String token, String role) {
    this.token = token;
    this.role = role;
  }

  // Getter and setter for user
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  // Getters and setters for token and role
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
