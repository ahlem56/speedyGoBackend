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

    // Bean for PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Use BCryptPasswordEncoder for password encoding
    }

    // Bean for AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)  // Use the UserService here
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception  {
        http.cors()
                .and()
                .csrf().disable()  // Disable CSRF for API use
                .authorizeRequests()
                .requestMatchers("/user/signup", "/user/signin", "/public/**" ,"/trip/**","/driver/**","/Admin/**","/parcel/**","/trip/getTripsForUser/","/subscription/**","/subscription/subscribeToSubscription/**","/user/forgot-password","/user/reset-password","/vehicle/**","/user/upload-profile-photo","/user/profile-photo/**","/trip/acceptTrip/**","/trip/refuseTrip/**","/user/update-profile","/payment/**","/partners/**", "/carpools/**","/carpools/get","/complaints/**").permitAll()// Allow these endpoints to be publicly accessible//.anyRequest().authenticated()
                .anyRequest().permitAll()// All other requests require authentication
                .and()
                .formLogin().disable()  // Disable Spring Security's default form login
                .httpBasic();  // Optionally enable HTTP Basic authentication

        return http.build();
    }


    }
