package tn.esprit.examen.nomPrenomClasseExamen.entities;

public class PaymentRequest {
    private Payment payment;
    private String sourceId;

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
