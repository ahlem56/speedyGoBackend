package tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
  private final SecretKey secretKey = Keys.hmacShaKeyFor(
          "your-256-bit-secret-key-here-must-be-32-chars".getBytes(StandardCharsets.UTF_8)
  ); // Secure key for HS256
  // 🔹 Générer un token avec le rôle
  public String generateToken(String username, String role,Long userId) {
    return Jwts.builder()
      .setSubject(username)
      .claim("role", role) // Ajout du rôle dans le token
            .claim("userId", userId)
      .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
      .signWith(secretKey, SignatureAlgorithm.HS256)
      .compact();
  }

  // 🔹 Extraire les claims du token
  public Claims extractClaims(String token) {
    JwtParser parser = Jwts.parser()  // Use the older parser method
      .setSigningKey(secretKey)  // Set the signing key
      .build();  // Build the parser
    return parser.parseClaimsJws(token).getBody();  // Parse the claims from the token
  }

  // 🔹 Extraire l'email de l'utilisateur
  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  // 🔹 Extraire le rôle de l'utilisateur
  public String extractRole(String token) {
    return extractClaims(token).get("role", String.class);
  }

  // 🔹 Vérifier si le token est expiré
  public boolean isTokenExpired(String token) {
    return extractClaims(token).getExpiration().before(new Date());
  }

  // 🔹 Valider le token
  public boolean validateToken(String token, String username) {
    return (username.equals(extractUsername(token)) && !isTokenExpired(token));
  }
}
