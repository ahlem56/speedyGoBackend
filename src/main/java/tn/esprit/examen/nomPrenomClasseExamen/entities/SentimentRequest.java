package tn.esprit.examen.nomPrenomClasseExamen.entities;

public class SentimentRequest {
    private String review;

    public SentimentRequest(String review) {
        this.review = review;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}

