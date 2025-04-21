package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Trip;
import tn.esprit.examen.nomPrenomClasseExamen.services.TripService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/emergency")
public class EmergencyController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TripService tripService;

    @PostMapping("/send-sos-email")
    public ResponseEntity<Object> sendSOSAlert(@RequestBody EmergencyRequest emergencyRequest) {
        try {
            // Extract user and location information from the request
            SimpleUser user = emergencyRequest.getSimpleUser();
            String userFirstName = user.getUserFirstName();
            String userLastName = user.getUserLastName();
            String userEmail = user.getUserEmail();
            String emergencyContactEmail = user.getEmergencyContactEmail();
            double latitude = emergencyRequest.getLatitude();
            double longitude = emergencyRequest.getLongitude();

            // Get the actual address of the passenger using Google Maps Geocoding API
            String locationMessage = getFormattedAddress(latitude, longitude);

            // Prepare email content
            String subject = "Emergency Alert: User in Danger!";
            String body = "User " + userFirstName + " " + userLastName + " (ID: " + user.getUserId() + ") is in danger. " +
                    "Location: " + locationMessage + ". Message: Please check immediately.";

            // Send email to the emergency contact
            sendEmail(subject, body, emergencyContactEmail);

            return ResponseEntity.ok(new ResponseMessage("SOS alert sent."));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Failed to send SOS alert."));
        }
    }

    // Function to fetch the address using Google Maps Geocoding API
    private String getFormattedAddress(double latitude, double longitude) {
        try {
            // Replace with your actual Google Maps API key
            String apiKey = "AIzaSyCZ1y4F3xvG8OW8O3IR8_-Rim--HLNGWUw";
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + apiKey;

            // Make a request to the Geocoding API
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the response to get the formatted address using JSON library
            String responseString = response.toString();
            String address = parseAddressFromResponse(responseString);

            return address;

        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to retrieve location";
        }
    }

    private String parseAddressFromResponse(String response) {
        try {
            // Parse the JSON response to get the formatted address
            org.json.JSONObject jsonResponse = new org.json.JSONObject(response);
            org.json.JSONArray results = jsonResponse.getJSONArray("results");

            if (results.length() > 0) {
                // Get the formatted address from the first result
                String formattedAddress = results.getJSONObject(0).getString("formatted_address");
                return formattedAddress;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unable to retrieve location";
    }

    private void sendEmail(String subject, String body, String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bouchahouaahlem@gmail.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace to the console
            throw new RuntimeException("Error sending email", e);
        }
    }

    // Helper class to return JSON response
    public static class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // Helper class to receive the SOS request data
    public static class EmergencyRequest {
        private SimpleUser simpleUser;
        private double latitude;
        private double longitude;
        private int tripId;  // Add this field
        private String tripDeparture;  // Add this field
        private String tripDestination; // Add this field
        private String tripDate; // Add this field

        public SimpleUser getSimpleUser() {
            return simpleUser;
        }

        public void setSimpleUser(SimpleUser simpleUser) {
            this.simpleUser = simpleUser;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public int getTripId() {
            return tripId;
        }

        public void setTripId(int tripId) {
            this.tripId = tripId;
        }

        public String getTripDeparture() {
            return tripDeparture;
        }

        public void setTripDeparture(String tripDeparture) {
            this.tripDeparture = tripDeparture;
        }

        public String getTripDestination() {
            return tripDestination;
        }

        public void setTripDestination(String tripDestination) {
            this.tripDestination = tripDestination;
        }

        public String getTripDate() {
            return tripDate;
        }

        public void setTripDate(String tripDate) {
            this.tripDate = tripDate;
        }
    }
}
