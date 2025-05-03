package tn.esprit.examen.nomPrenomClasseExamen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import tn.esprit.examen.nomPrenomClasseExamen.entities.PaymentMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Data
public class PaymentRequestDTO {
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal paymentAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date paymentDate;

    @NotNull
<<<<<<< HEAD:src/main/java/tn/esprit/examen/nomPrenomClasseExamen/DTO/PaymentRequestDTO.java
    private PaymentMethod  paymentMethod;
=======
    private PaymentMethod paymentMethod;
>>>>>>> recoveryAhlem:src/main/java/tn/esprit/examen/nomPrenomClasseExamen/dto/PaymentRequestDTO.java
    private Integer userId;

    private String stripePaymentMethodId;

    private Integer tripId;
    private Integer parcelId;
    private Integer partnerId;

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount != null ? paymentAmount.setScale(2, RoundingMode.HALF_UP) : null;
    }

    // Add setter for string-based paymentMethod
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod != null ? PaymentMethod.valueOf(paymentMethod.toUpperCase()) : null;
    }
}