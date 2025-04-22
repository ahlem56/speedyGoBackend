package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
// ✅ This is YOUR User entity
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity.JwtUtil;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.UserRepository;

@AllArgsConstructor
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;


    public User getUserByEmail(String email) {
        return userRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        tn.esprit.examen.nomPrenomClasseExamen.entities.User user = userRepository.findByUserEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        System.out.println("User password: " + user.getUserPassword());
        String role = "USER";
        if (user instanceof Admin) role = "ADMIN";
        else if (user instanceof Driver) role = "DRIVER";

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserEmail())
                .password(user.getUserPassword())
                .roles(role)
                .build();
    }




    // Implementing the method to update the user's profile photo
    public void updateProfilePhoto(String username, String fileName) {
        // Retrieve the user from the database
        tn.esprit.examen.nomPrenomClasseExamen.entities.User user = userRepository.findByUserEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Update the user's profile photo field
        user.setUserProfilePhoto(fileName);

        // Save the updated user object in the database
        userRepository.save(user);
        System.out.println("✅ Profile photo updated in DB for: " + username);

    }


}
