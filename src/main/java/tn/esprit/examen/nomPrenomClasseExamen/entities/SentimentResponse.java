package tn.esprit.examen.nomPrenomClasseExamen.entities;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SentimentResponse {
    private String sentiment;

    @JsonProperty("predicted_score")
    private double predictedScore;

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public double getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(double predictedScore) {
        this.predictedScore = predictedScore;
    }
}
