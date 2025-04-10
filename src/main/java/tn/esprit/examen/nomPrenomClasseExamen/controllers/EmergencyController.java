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
import tn.esprit.examen.nomPrenomClasseExamen.entities.User;
import tn.esprit.examen.nomPrenomClasseExamen.services.TripService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/emergency")
public class EmergencyController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TripService tripService;

    @PostMapping("/send-sos-email")
    public ResponseEntity<Object> sendSOSAlert(@RequestBody Trip trip) {
        try {
            // Extract user information from the trip
            SimpleUser user = trip.getSimpleUser();
            String userFirstName = user.getUserFirstName();
            String userLastName = user.getUserLastName();
            String userEmail = user.getUserEmail();
            String emergencyContactEmail = user.getEmergencyContactEmail();
            String locationMessage = "Latitude: " + trip.getLatitude() + ", Longitude: " + trip.getLongitude();
            System.out.println("Emergency Contact Email: " + emergencyContactEmail);

            // Find the active trip based on the current date
            Trip activeTrip = getActiveTripForUser(user);
            if (activeTrip == null) {
                System.out.println("No active trip found for this user.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("No active trip found for this user."));
            }

            // Prepare email content
            String subject = "Emergency Alert: User in Danger!";
            String body = "User " + userFirstName + " " + userLastName + " (ID: " + user.getUserId() + ") is in danger. " +
                    "Location: " + locationMessage + ". Message: " + "Please check immediately.";

            // Send email to the emergency contact
            sendEmail(subject, body, emergencyContactEmail);

            System.out.println("SOS alert sent successfully to: " + emergencyContactEmail);
            return ResponseEntity.ok(new ResponseMessage("SOS alert sent."));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Failed to send SOS alert."));
        }
    }


    private Trip getActiveTripForUser(SimpleUser user) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<Trip> trips = tripService.findTripsBySimpleUser(user);

        System.out.println("Current date: " + currentDate);
        for (Trip trip : trips) {
            LocalDateTime tripStartDate = trip.getTripDate();

            // Parse the trip duration to extract hours and minutes
            String tripDuration = trip.getTripDuration();
            long durationInMinutes = parseDurationToMinutes(tripDuration);

            LocalDateTime tripEndDate = tripStartDate.plusMinutes(durationInMinutes);

            System.out.println("Checking trip: " + trip.getTripId() + ", Start: " + tripStartDate + ", End: " + tripEndDate);

            if (tripStartDate.isBefore(currentDate) && tripEndDate.isAfter(currentDate)) {
                return trip;
            }
        }
        return null;
    }

    private long parseDurationToMinutes(String duration) {
        long minutes = 0;

        // Regex to match hours and minutes in the format of "X hour(s) Y min(s)"
        Pattern pattern = Pattern.compile("(\\d+)\\s*hour.*?(\\d+)\\s*min");
        Matcher matcher = pattern.matcher(duration);

        if (matcher.find()) {
            // Extract hours and minutes
            long hours = Long.parseLong(matcher.group(1));
            long mins = Long.parseLong(matcher.group(2));
            minutes = (hours * 60) + mins;
        }
        return minutes;
    }



    private void sendEmail(String subject, String body, String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bouchahaouaahlem@gmail.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Error sending email to: " + toEmail);
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


}
