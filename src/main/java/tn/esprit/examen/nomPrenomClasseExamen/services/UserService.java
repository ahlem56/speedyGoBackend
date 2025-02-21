package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.UserRepository;

@AllArgsConstructor
@Service
public class UserService implements IUserService, org.springframework.security.core.userdetails.UserDetailsService {
@Autowired
    private  UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        tn.esprit.examen.nomPrenomClasseExamen.entities.User user = userRepository.findByUserEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Log the password to check the comparison
        System.out.println("User password: " + user.getUserPassword());

        String role = "USER"; // Default role
        if (user instanceof Admin) {
            role = "ADMIN";
        } else if (user instanceof Driver) {
            role = "DRIVER";
        }

        return User.withUsername(user.getUserEmail())
                .password(user.getUserPassword())
                .roles(role)
                .build();
    }

}
