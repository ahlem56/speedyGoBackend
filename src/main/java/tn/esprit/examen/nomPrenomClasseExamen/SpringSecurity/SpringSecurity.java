package tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.examen.nomPrenomClasseExamen.services.UserService;

@Configuration
public class SpringSecurity {

    private final UserService userService;

    public SpringSecurity(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .requestMatchers("/user/signup", "/user/signin", "/event/**", "/public/**", "/trip/**", "/driver/**", "/Admin/**", "/parcel/**", "/subscription/**", "/user/forgot-password", "/user/reset-password", "/vehicle/**", "/user/upload-profile-photo", "/user/profile-photo/**", "/trip/acceptTrip/**", "/trip/refuseTrip/**", "/user/update-profile", "/partners/**", "/carpools/**", "/carpools/get", "/complaints/**", "/completeTrip/*", "/ratings/rate/*", "/sendSos", "/ws-chat", "/chat", "/user/test-payment/**", "/commission/**", "/predict_price", "/payments/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .formLogin().disable()
                .httpBasic();
        return http.build();
    }
}