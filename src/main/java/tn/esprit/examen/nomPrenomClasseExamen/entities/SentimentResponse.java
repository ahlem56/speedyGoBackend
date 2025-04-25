package tn.esprit.examen.nomPrenomClasseExamen.entities;

public class SentimentResponse {
    private String sentiment;
    private double score;

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
