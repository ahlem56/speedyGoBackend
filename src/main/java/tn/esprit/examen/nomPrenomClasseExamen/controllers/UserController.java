package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity.ApiResponse;
import tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity.JwtResponse;
import tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity.JwtUtil;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Payment;
import tn.esprit.examen.nomPrenomClasseExamen.entities.PaymentRequest;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.PaymentService;
import tn.esprit.examen.nomPrenomClasseExamen.services.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RequestMapping("/user")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final SimpleUserRepository simpleUserRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private UserService userService;
    private JavaMailSender mailSender;  // Injection du mailSender ici
    private PaymentService paymentService;  // Injection du paymentService ici


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

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByUserEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Generate a unique token for password reset
        String resetToken = UUID.randomUUID().toString();

        // Save the reset token in the database (You should also implement this part in your database)
        user.setResetToken(resetToken);
        userRepository.save(user);

        // Send password reset email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" +
                "http://localhost:4200/reset-password?token=" + resetToken);

        mailSender.send(message);

        return ResponseEntity.ok("Password reset email sent");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        // Find the user by reset token
        User user = userRepository.findByResetToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token");
        }

        // Encode the new password before saving
        user.setUserPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);  // Clear the reset token after successful reset
        userRepository.save(user);

        return ResponseEntity.ok("Password successfully reset");
    }


    /*@PostMapping("/upload-profile-photo")
    public ResponseEntity<String> uploadProfilePhoto(@RequestParam("profilePhoto") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String fileName = saveFile(file);  // Save the file to a directory or cloud storage
            // Update the user's profile photo in the database
            userService.updateProfilePhoto(userDetails.getUsername(), fileName); // Update the user's profile photo in the DB
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Path path = Paths.get("uploads/" + fileName);
        Files.write(path, file.getBytes());
        return fileName;  // Returning the file name to update the user's profile photo
    }
*/

    @PostMapping("/test-payment")
    public ResponseEntity<String> testPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            Payment payment = paymentService.processTestPayment(paymentRequest);
            return ResponseEntity.ok("Payment successful with charge ID: " + payment.getStripeChargeId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed: " + e.getMessage());
        }
    }

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            // Call the service method to process the payment
            Payment payment = paymentService.processTestPayment(paymentRequest);
            return ResponseEntity.ok("Payment processed successfully with ID: " + payment.getStripeChargeId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment processing failed: " + e.getMessage());
        }
    }

}
