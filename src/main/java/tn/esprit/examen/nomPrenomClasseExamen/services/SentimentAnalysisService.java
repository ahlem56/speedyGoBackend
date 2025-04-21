package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SentimentRequest;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SentimentResponse;
@Slf4j

@Service
public class SentimentAnalysisService {

    private final String sentimentAnalysisApiUrl = "http://127.0.0.1:5000/analyze";  // URL of your Flask API

    public SentimentResponse analyzeSentiment(String comment) {
        RestTemplate restTemplate = new RestTemplate();
        SentimentRequest request = new SentimentRequest(comment);
        SentimentResponse response = restTemplate.postForObject(sentimentAnalysisApiUrl, request, SentimentResponse.class);
        log.info("score : "+ response.getPredicted_score());
        // Check if sentiment score is in a valid range and return response
        if (response.getPredicted_score() < 1 || response.getPredicted_score() > 5) {
            // Default to neutral if the score is out of bounds
            response.setPredicted_score(3);

        }

        return response;
    }
}

