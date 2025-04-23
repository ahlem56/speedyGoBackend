package tn.esprit.examen.nomPrenomClasseExamen.services;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@AllArgsConstructor
@Service
public class IAParcelEstimatorService {
  private final RestTemplate restTemplate = new RestTemplate();
  private final String AI_URL = "http://localhost:5000/predict_price"; // L'URL de ton API Python

  public float getEstimatedPrice(float weight, String category) {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("weight", weight);
    requestBody.put("category", category);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(AI_URL, requestEntity, Map.class);
      Object predictedPriceObj = response.getBody().get("predicted_price");
      float predictedPrice = Float.parseFloat(predictedPriceObj.toString());
      return predictedPrice;

    } catch (Exception e) {
      log.error("Erreur lors de l'appel à l'IA: {}", e.getMessage());
      return -1; // Tu peux renvoyer un prix par défaut ou une exception selon ta stratégie
    }
  }
}
