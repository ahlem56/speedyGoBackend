package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "http://localhost:4200")  // Allow Angular frontend to access
@RestController
@RequestMapping("/maps")
public class MapsController {

    private final RestTemplate restTemplate;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public MapsController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/directions")
    public ResponseEntity<String> getDirections(@RequestParam String origin,
                                                @RequestParam String destination,
                                                @RequestParam(required = false) String waypoints) {
        String googleMapsUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin
                + "&destination=" + destination
                + "&departure_time=now"  // Ensures traffic is considered in the response
                + "&key=" + apiKey;

        if (waypoints != null && !waypoints.isEmpty()) {
            googleMapsUrl += "&waypoints=" + waypoints + "&optimize_waypoints=true";
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    googleMapsUrl,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            return response; // Returning the response from Google Maps API
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error contacting Google Maps API: " + e.getMessage());
        }
    }

}
