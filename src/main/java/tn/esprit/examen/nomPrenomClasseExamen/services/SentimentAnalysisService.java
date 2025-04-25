package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SentimentRequest;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SentimentResponse;

@Service
public class SentimentAnalysisService {

    private final String sentimentAnalysisApiUrl = "http://127.0.0.1:5000/analyze";  // URL of your Flask API

    public String analyzeSentiment(String comment) {
        // Use RestTemplate to make the API call to Flask sentiment analysis API
        RestTemplate restTemplate = new RestTemplate();
        SentimentRequest request = new SentimentRequest(comment);
        SentimentResponse response = restTemplate.postForObject(sentimentAnalysisApiUrl, request, SentimentResponse.class);
        return response.getSentiment();
    }
}