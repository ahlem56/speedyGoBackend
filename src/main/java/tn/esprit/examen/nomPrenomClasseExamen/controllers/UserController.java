package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity.ApiResponse;
import tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity.JwtResponse;
import tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity.JwtUtil;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.UserRepository;

@AllArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

    private final SimpleUserRepository simpleUserRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SimpleUser simpleUser) {
        if (simpleUser.getUserPassword() == null || simpleUser.getUserPassword().isEmpty()) {
            return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }

        // Encode the password before saving
        simpleUser.setUserPassword(passwordEncoder.encode(simpleUser.getUserPassword()));

        // Save the new user to the database
        simpleUserRepository.save(simpleUser);

        // Return a structured JSON response
        ApiResponse apiResponse = new ApiResponse("User created successfully", true);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);  // Ensure a structured response
    }





    // Sign in
//    @PostMapping("/signin")
//    public ResponseEntity<JwtResponse> signIn(@RequestBody SimpleUser simpleUser) {
//        // Find the SimpleUser by email
//        SimpleUser user = simpleUserRepository.findByUserEmail(simpleUser.getUserEmail());
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//
//        // Compare the raw password with the encoded password
//        if (!passwordEncoder.matches(simpleUser.getUserPassword(), user.getUserPassword())) {
//            throw new BadCredentialsException("Invalid credentials");
//        }
//
//        // Generate JWT token
//        String token = jwtUtil.generateToken(user.getUserEmail());
//
//        // Return token wrapped in JwtResponse (in JSON format)
//        return ResponseEntity.ok(new JwtResponse("Bearer " + token));  // Return the token in a JSON object
//    }
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> signIn(@RequestBody SimpleUser simpleUser) {
      // Récupérer l'utilisateur par email
      User user = userRepository.findByUserEmail(simpleUser.getUserEmail());
              if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }


      // Vérifier le mot de passe
      if (!passwordEncoder.matches(simpleUser.getUserPassword(), user.getUserPassword())) {
        throw new BadCredentialsException("Invalid credentials");
      }

      // Déterminer dynamiquement la sous-classe de l'utilisateur
      String role = getRoleFromUser(user);

      // Générer le token avec le rôle
      String token = jwtUtil.generateToken(user.getUserEmail(), role);

      // Retourner le token et le rôle
        JwtResponse response = new JwtResponse("Bearer " + token, role);
        response.setUser(user);  // Add the user object to the response
        return ResponseEntity.ok(response);


    }

  // Méthode pour détecter la sous-classe de l'utilisateur
  private String getRoleFromUser(User user) {
    if (user instanceof Admin) {
      return "Admin";
    } else if (user instanceof Driver) {
      return "Driver";
    } else if (user instanceof SimpleUser) {
      return "SimpleUser";
    }
    return "Unknown";
  }


}
