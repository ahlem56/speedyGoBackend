package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SeverityRequest;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SeverityResponse;

@Slf4j
@Service
public class SeverityAnalysisService {

    private final String severityAnalysisApiUrl = "http://localhost:5000/analyze"; // Update with your Flask API URL
    private final RestTemplate restTemplate;

    public SeverityAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private boolean isValidSeverity(String severity) {
        if (severity == null) return false;
        String normalizedSeverity = severity.toLowerCase();
        return normalizedSeverity.equals("high") ||
                normalizedSeverity.equals("medium") ||
                normalizedSeverity.equals("low") ||
                normalizedSeverity.equals("unknown");
    }

    public SeverityResponse analyzeSeverity(String complaintDescription) {
        try {
            SeverityRequest request = new SeverityRequest(complaintDescription);
            log.info("Sending request to Flask API: {}", request);
            SeverityResponse response = restTemplate.postForObject(
                    severityAnalysisApiUrl, request, SeverityResponse.class);
            log.info("Received response from Flask API: {}", response);

            if (response == null || response.getPredictedSeverity() == null) {
                log.warn("Invalid or null response from severity analysis API");
                return createFallbackResponse(complaintDescription);
            }

            // Normalize severity to lowercase
            String normalizedSeverity = response.getPredictedSeverity().toLowerCase();
            response.setPredictedSeverity(normalizedSeverity);

            if (!isValidSeverity(normalizedSeverity)) {
                log.warn("Invalid severity value: {}. Defaulting to 'unknown'", normalizedSeverity);
                response.setPredictedSeverity("unknown");
            }

            log.info("Predicted severity: {}", response.getPredictedSeverity());
            return response;

        } catch (HttpClientErrorException e) {
            log.error("HTTP error calling severity analysis API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return createFallbackResponse(complaintDescription);
        } catch (RestClientException e) {
            log.error("Rest client error calling severity analysis API: {}", e.getMessage(), e);
            return createFallbackResponse(complaintDescription);
        } catch (Exception e) {
            log.error("Unexpected error calling severity analysis API: {}", e.getMessage(), e);
            return createFallbackResponse(complaintDescription);
        }
    }

    private SeverityResponse createFallbackResponse(String text) {
        SeverityResponse fallback = new SeverityResponse();
        fallback.setText(text);
        fallback.setPredictedSeverity("unknown");
        fallback.setConfidence("Low");
        return fallback;
    }
}