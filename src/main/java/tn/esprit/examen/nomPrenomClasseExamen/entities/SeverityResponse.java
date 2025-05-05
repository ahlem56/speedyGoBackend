package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

@Data
public class SeverityResponse {
    private String text;
    @JsonProperty("processed_text")
    private String processedText;
    @JsonProperty("predicted_severity")
    private String predictedSeverity;
    private Map<String, Double> probabilities;
    private String confidence;
}