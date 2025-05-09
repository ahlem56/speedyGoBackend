package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity.UploadResponse;
import tn.esprit.examen.nomPrenomClasseExamen.entities.*;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.PartnersRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.UserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.services.PaymentService;
import tn.esprit.examen.nomPrenomClasseExamen.services.RatingService;
import tn.esprit.examen.nomPrenomClasseExamen.services.UserService;
import tn.esprit.examen.nomPrenomClasseExamen.dto.UserProfile;
import tn.esprit.examen.nomPrenomClasseExamen.dto.PartnerDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@RequestMapping("/user")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final SimpleUserRepository simpleUserRepository;
    private final AuthenticationManager authenticationManager;
    private final PartnersRepository partnersRepository;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private UserService userService;
    private JavaMailSender mailSender;
    private PaymentService paymentService;
    private RatingService ratingService;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SimpleUser simpleUser) {
        if (simpleUser.getUserPassword() == null || simpleUser.getUserPassword().isEmpty()) {
            return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (simpleUser.getUserPassword().length() < 6) {
            return new ResponseEntity<>("Password must be at least 6 characters", HttpStatus.BAD_REQUEST);
        }
        if (simpleUser.getUserFirstName() == null || simpleUser.getUserFirstName().isEmpty()) {
            return new ResponseEntity<>("First Name is required", HttpStatus.BAD_REQUEST);
        }
        if (simpleUser.getUserLastName() == null || simpleUser.getUserLastName().isEmpty()) {
            return new ResponseEntity<>("Last Name is required", HttpStatus.BAD_REQUEST);
        }
        if (simpleUser.getUserCin() == null || simpleUser.getUserCin() <= 0) {
            return new ResponseEntity<>("CIN must be a valid positive number", HttpStatus.BAD_REQUEST);
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(simpleUser.getUserEmail());
        if (!matcher.matches()) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByUserEmail(simpleUser.getUserEmail()) != null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.BAD_REQUEST);
        }
        simpleUser.setUserPassword(passwordEncoder.encode(simpleUser.getUserPassword()));
        simpleUserRepository.save(simpleUser);
        ApiResponse apiResponse = new ApiResponse("User created successfully", true);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> signIn(@RequestBody SimpleUser simpleUser) {
        User user = userRepository.findByUserEmail(simpleUser.getUserEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!passwordEncoder.matches(simpleUser.getUserPassword(), user.getUserPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String role = getRoleFromUser(user);
        String token = jwtUtil.generateToken(user.getUserEmail(), role, user.getUserId().longValue());
        JwtResponse response = new JwtResponse("Bearer " + token, role);
        response.setUser(user);
        return ResponseEntity.ok(response);
    }

    private String getRoleFromUser(User user) {
        if (user instanceof Admin) {
            return "ADMIN";
        } else if (user instanceof Driver) {
            return "DRIVER";
        } else if (user instanceof SimpleUser) {
            return "SimpleUser";
        }
        return "SimpleUser";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByUserEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);
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
        User user = userRepository.findByResetToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token");
        }
        user.setUserPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
        return ResponseEntity.ok("Password successfully reset");
    }

    @PostMapping("/upload-profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("profilePhoto") MultipartFile file,
                                                @RequestHeader("Authorization") String token) {
        System.out.println("🔹 Received Token: " + token);
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Missing or invalid token"));
        }
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);
        System.out.println("🔹 Extracted Email from Token: " + email);
        User user = userRepository.findByUserEmail(email);
        if (user == null) {
            System.err.println("❌ ERROR: User not found for email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        try {
            String fileName = saveFile(file);
            System.out.println("✅ Saved File: " + fileName);
            userService.updateProfilePhoto(email, fileName);
            return ResponseEntity.ok(Map.of("fileName", fileName));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "File upload failed"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepository.findByUserEmail(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile-photo/{fileName}")
    public ResponseEntity<Resource> getProfilePhoto(@PathVariable String fileName) {
        try {
            Path path = Paths.get("uploads/" + fileName);
            Resource resource = new FileSystemResource(path.toFile());
            if (resource.exists() || resource.isReadable()) {
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                MediaType mediaType = switch (extension) {
                    case "png" -> MediaType.IMAGE_PNG;
                    case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
                    case "gif" -> MediaType.IMAGE_GIF;
                    default -> MediaType.APPLICATION_OCTET_STREAM;
                };
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Path path = Paths.get("uploads/" + fileName);
        Path dirPath = Paths.get("uploads/");
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            System.out.println("Directory 'uploads/' created successfully.");
        }
        try {
            Files.write(path, file.getBytes());
            System.out.println("File saved successfully at: " + path.toString());
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            throw new IOException("Error saving file: " + e.getMessage());
        }
        return fileName;
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> updatedData) {
        String jwt = token.substring(7);
        String email = jwtUtil.extractUsername(jwt);
        User user = userRepository.findByUserEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (updatedData.containsKey("firstName")) {
            user.setUserFirstName(updatedData.get("firstName"));
        }
        if (updatedData.containsKey("lastName")) {
            user.setUserLastName(updatedData.get("lastName"));
        }
        if (updatedData.containsKey("email")) {
            user.setUserEmail(updatedData.get("email"));
        }
        if (updatedData.containsKey("address")) {
            user.setUserAddress(updatedData.get("address"));
        }
        if (updatedData.containsKey("birthDate")) {
            try {
                user.setUserBirthDate(java.sql.Date.valueOf(updatedData.get("birthDate")));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid birthDate format (yyyy-MM-dd)");
            }
        }
        if (updatedData.containsKey("partnerId")) {
            try {
                Integer partnerId = Integer.parseInt(updatedData.get("partnerId"));
                Partners partner = partnersRepository.findById(partnerId)
                        .orElseThrow(() -> new IllegalArgumentException("Partner not found"));
                user.setPartner(partner);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid partnerId");
            }
        }
        System.out.println("User type before save: " + (user instanceof SimpleUser ? "SimpleUser" : user instanceof Admin ? "Admin" : "Driver"));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    @GetMapping("/partners")
    public List<User> getPartners() {
        return userRepository.findByPartnerNotNull();
    }

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
            Payment payment = paymentService.processTestPayment(paymentRequest);
            return ResponseEntity.ok("Payment processed successfully with ID: " + payment.getStripeChargeId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment processing failed: " + e.getMessage());
        }
    }

    @GetMapping("/top-drivers")
    public ResponseEntity<List<Driver>> getTopDrivers(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ratingService.getTopRatedDrivers(limit));
    }

    @GetMapping("/top-passengers")
    public ResponseEntity<List<User>> getTopPassengers(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ratingService.getTopRatedPassengers(limit));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> getCurrentUser(@RequestHeader("Authorization") String token) {
        System.out.println("🔍 Received Token for /me: " + token);
        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println("❌ Invalid token format");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String jwt = token.substring(7);
        String email;
        try {
            email = jwtUtil.extractUsername(jwt);
            System.out.println("🔍 Extracted Email: " + email);
        } catch (Exception e) {
            System.out.println("❌ Token parsing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepository.findByUserEmail(email);
        if (user == null) {
            System.out.println("❌ User not found for email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getUserId());
        profile.setEmail(user.getUserEmail());
        if (user instanceof SimpleUser simpleUser && simpleUser.getPartners() != null) {
            PartnerDTO partner = new PartnerDTO();
            partner.setPartnerId(simpleUser.getPartners().getPartnerId());
            partner.setPartnerName(simpleUser.getPartners().getPartnerName());
            profile.setPartner(partner);
        }
        System.out.println("✅ Returning profile for user: " + email);
        return ResponseEntity.ok(profile);
    }
}