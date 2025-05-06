package tn.esprit.examen.nomPrenomClasseExamen.dto;

public class AssignPartnerRequest {
    private Integer userId;
    private Integer partnerId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }
}