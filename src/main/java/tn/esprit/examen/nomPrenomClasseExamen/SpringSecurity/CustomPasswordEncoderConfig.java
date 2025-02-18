package tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CustomPasswordEncoderConfig {

    @Bean(name = "customPasswordEncoder") // Rename the bean to avoid conflict
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt is the recommended password encoder
    }
}
