package tn.esprit.examen.nomPrenomClasseExamen.entities;

public class SentimentResponse {
    private String sentiment;
    private double predicted_score;

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public double getPredicted_score() {
        return predicted_score;
    }

    public void setPredicted_score(double predicted_score) {
        this.predicted_score = predicted_score;
    }
}
