package tn.esprit.examen.nomPrenomClasseExamen.SpringSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/examen/ws-chat")
                .allowedOrigins("http://localhost:4200")  // Allow requests from Angular
                .allowedMethods("GET", "POST", "OPTIONS");

        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // Ensure Angular app is allowed
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);// Allow credentials if necessary
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/examen/user/upload-profile-photo/**")
                .addResourceLocations("file:/path/to/uploads/");
      // Serve les images des colis endommagés depuis le sous-dossier damaged-parcels
      registry.addResourceHandler("/examen/parcel/{id}/report-damage/**")  // URL frontend pour accéder aux fichiers endommagés
        .addResourceLocations("file:/path/to/uploads/damaged-parcels/");  // Chemin absolu vers le sous-dossier damaged-parcels
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
