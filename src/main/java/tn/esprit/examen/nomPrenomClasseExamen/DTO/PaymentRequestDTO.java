package tn.esprit.examen.nomPrenomClasseExamen.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import tn.esprit.examen.nomPrenomClasseExamen.entities.PaymentMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Data
public class PaymentRequestDTO {
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal paymentAmount;


    private Date paymentDate;

    @NotNull
    private PaymentMethod  paymentMethod;
    private Integer userId;

    private String stripePaymentMethodId;

    private Integer tripId;
    private Integer parcelId;

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
