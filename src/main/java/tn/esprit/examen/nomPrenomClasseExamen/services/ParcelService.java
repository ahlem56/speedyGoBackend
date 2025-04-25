package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.DriverRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ParcelRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class ParcelService implements IParcelService {
  @Autowired
  private ParcelRepository parcelRepository;
  @Autowired
  private DriverRepository driverRepository;
  @Autowired
  private SimpleUserRepository simpleUserRepository;
  @Autowired
  private IAParcelEstimatorService iaParcelEstimatorService;

//    @Override
//    public Parcel createParcel(Parcel parcel) {
//        return parcelRepository.save(parcel);
//    }

  @Override
  public Parcel createParcel(Parcel parcel, Integer userId) {
    SimpleUser user = simpleUserRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("SimpleUser not found with id: " + userId));

    parcel.setSimpleUser(user);
    parcel.setParcelDate(new Date());

    // 💡 Utiliser le modèle IA pour estimer le prix
    float estimatedPrice = iaParcelEstimatorService.getEstimatedPrice(parcel.getParcelWeight(), parcel.getParcelCategory().toString());
    parcel.setParcelPrice(estimatedPrice);

    parcel.setStatus(Status.PENDING);
    return parcelRepository.save(parcel);
  }



  @Override
  public Parcel assignParcelToDriver(Integer parcelId, Integer driverId) {
// Vérifier si le colis existe
    Parcel parcel = parcelRepository.findById(parcelId)
      .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + parcelId));

    // Vérifier si le Driver existe
    Driver driver = driverRepository.findById(driverId)
      .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

    // Affecter le chauffeur au colis
    parcel.setDriver(driver);

    return parcelRepository.save(parcel);  }

  //  @Override
//  public Parcel createParcelWithAssignment(Parcel parcel, Integer driverId, Integer userId) {
//    // Check if the SimpleUser exists
//    SimpleUser user = simpleUserRepository.findById(userId)
//      .orElseThrow(() -> new RuntimeException("SimpleUser not found with id: " + userId));
//
//    // Check if the Driver exists
//    Driver driver = driverRepository.findById(driverId)
//      .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
//
//    // Set the associations
//    parcel.setSimpleUser(user);//affectation to a user
//    parcel.setDriver(driver); //affectation to a driver
//    parcel.setParcelDate(new Date());  // Set the current date for the parcel
//
//    // Save the parcel and return the saved entity
//    return parcelRepository.save(parcel);
//  }
  @Override
  public Parcel getParcelById(Integer id) {
    return parcelRepository.findById(id).orElseThrow(() -> new RuntimeException("Parcel not found"));
  }

  @Override
  public List<Parcel> getAllParcels() {
    return parcelRepository.findAll();    }

  @Override
  public Parcel updateParcel(Integer id, Parcel parcelDetails) {
    Parcel parcel = getParcelById(id);
    parcel.setParcelCategory(parcelDetails.getParcelCategory());
    parcel.setRecepeientPhoneNumber(parcelDetails.getRecepeientPhoneNumber());
    parcel.setSenderPhoneNumber(parcelDetails.getSenderPhoneNumber());
    parcel.setParcelDeparture(parcelDetails.getParcelDeparture());
    parcel.setParcelDestination(parcelDetails.getParcelDestination());
    parcel.setParcelWeight(parcelDetails.getParcelWeight());
    parcel.setParcelDate(parcelDetails.getParcelDate());
    parcel.setParcelPrice(parcelDetails.getParcelPrice());
    parcel.setParcelPrice(determineParcelPrice(parcel.getParcelWeight()));
    parcel.setDriver(parcelDetails.getDriver());
    parcel.setSimpleUser(parcelDetails.getSimpleUser());
    return parcelRepository.save(parcel);
  }
//pour recuperer les parcels by driver
  public List<Parcel> getParcelsByDriver(Long driverId) {
    return parcelRepository.findByDriver_Id(driverId);
  }
  @Override
  public void deleteParcel(Integer id) {
    Parcel parcel = getParcelById(id);
    parcelRepository.delete(parcel);
  }

  @Override
  public List<Parcel> getParcelsForUser(Integer userId) {
    SimpleUser user = simpleUserRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));
    return parcelRepository.findBySimpleUser(user);
  }
  // Filtrer les colis créés après une certaine date
  public List<Parcel> getParcelsAfterDate(Date date) {
    return parcelRepository.findParcelsAfterDate(date);
  }

  // Filtrer les colis créés avant une certaine date
  public List<Parcel> getParcelsBeforeDate(Date date) {
    return parcelRepository.findParcelsBeforeDate(date);
  }

  @Override
  public float determineParcelPrice(double weight) {
    if (weight > 20) {
      return 30.0F; // Plus de 20 KG
    } else if (weight >= 5) {
      return 20.0F; // Entre 5 et 20 KG
    } else {
      return 10.0F; // Entre 0.1 et 5 KG
    }
  }

  @Override
  public long getDeliveredParcelsByDay(Date date) {
    return parcelRepository.countDeliveredParcelsByDay(date);
  }

  @Override
  public long getDeliveredParcelsByWeek(Date date) {
    return parcelRepository.countDeliveredParcelsByWeek(date);
  }

  @Override
  public long getDeliveredParcelsByMonth(Date date) {
    return parcelRepository.countDeliveredParcelsByMonth(date);
  }

  @Override
  public Parcel markAsShipped(Long parcelId) throws Exception {
    // Si votre repository utilise Integer comme clé, convertissez le Long en Integer
    Parcel parcel = parcelRepository.findById(parcelId.intValue())
      .orElseThrow(() -> new Exception("Parcel not found with id: " + parcelId));
    parcel.setStatus(Status.SHIPPED);
    return parcelRepository.save(parcel); // ← retourne le colis mis à jour

  }

  @Override
  public void markAsDelivered(Long parcelId) throws Exception {
    // Si votre repository utilise Integer comme clé, convertissez le Long en Integer
    Parcel parcel = parcelRepository.findById(parcelId.intValue())
      .orElseThrow(() -> new Exception("Parcel not found with id: " + parcelId));
    parcel.setStatus(Status.DELIVERED);
    parcelRepository.save(parcel);
  }

  @Override
  public List<Parcel> archiveDeliveredParcels() {
    List<Parcel> deliveredParcels = parcelRepository.findByStatusAndArchivedFalse(Status.DELIVERED);
    for (Parcel parcel : deliveredParcels) {
      parcel.setArchived(true);
      parcelRepository.save(parcel);
    }
    return deliveredParcels;  }

  @Override
  public List<Parcel> getParcelsByStatus(Status status) {
    return parcelRepository.findByStatus(status);

  }

  @Override
  public Map<String, Float> calculateYearlyRevenue() {
    List<Parcel> parcels = parcelRepository.findAll(); // Récupérer tous les colis

    // Initialiser un LinkedHashMap pour conserver l'ordre des mois
    Map<String, Float> monthlyRevenue = new LinkedHashMap<>();
    String[] months = {
      "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
      "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    };

    // Initialiser chaque mois à 0
    for (String month : months) {
      monthlyRevenue.put(month, 0f);
    }

    // Filtrer et calculer le revenu pour chaque mois
    for (Parcel parcel : parcels) {
      int month = parcel.getParcelDate().getMonth();  // Obtenir le mois
      float price = parcel.getParcelPrice();
      String monthName = months[month];  // Convertir le mois en nom

      // Ajouter le prix du colis au revenu du mois
      monthlyRevenue.put(monthName, monthlyRevenue.get(monthName) + price);
    }

    return monthlyRevenue;
  }

  @Override
  public Parcel save(Parcel parcel) {
    return parcelRepository.save(parcel);
  }

  @Override
  public List<Parcel> searchParcelsForDriver(Long driverId, String departure, String destination) {
    return null;
  }

  @Override
  public Parcel findParcelById(Integer parcelId) {
    return parcelRepository.findById(parcelId).orElse(null);  // Retourne null si non trouvé
  }
//Notification features

  //
//  public String saveDamageImage(Integer parcelId, MultipartFile image, String description) throws IOException {
//    Parcel parcel = parcelRepository.findById(parcelId)
//      .orElseThrow(() -> new RuntimeException("Parcel not found"));
//
//    // Stocke l'image quelque part
//    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
//    Path imagePath = Paths.get("uploads/damaged-parcels", fileName);
//    Files.createDirectories(imagePath.getParent());
//    Files.write(imagePath, image.getBytes());
//    String imageUrl = "/uploads/damaged-parcels/" + fileName;
//    // Mets à jour le colis
//    parcel.setDamageImageUrl(imagePath.toString());
//    parcel.setDamageDescription(description);
//    parcel.setDamageReportedAt(LocalDateTime.now());
//
//    parcelRepository.save(parcel);
//
//    return imageUrl;
//  }
  public String saveDamageImage(Integer parcelId, MultipartFile image, String description) throws IOException {
    Parcel parcel = parcelRepository.findById(parcelId)
      .orElseThrow(() -> new RuntimeException("Parcel not found"));

    // Stocke l'image quelque part
    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
    Path imagePath = Paths.get("uploads/damaged-parcels", fileName);  // Chemin physique sur le disque

    Files.createDirectories(imagePath.getParent());  // Créer les répertoires si nécessaire
    Files.write(imagePath, image.getBytes());  // Enregistrer l'image

    // Utilisation du chemin relatif pour l'URL
    String imageUrl = "/uploads/damaged-parcels/" + fileName;  // URL accessible depuis le frontend

    // Mets à jour le colis
    parcel.setDamageImageUrl(imageUrl);  // Enregistrer le chemin relatif dans la base de données
    parcel.setDamageDescription(description);
    parcel.setDamageReportedAt(LocalDateTime.now());

    parcelRepository.save(parcel);

    return imageUrl;  // Retourner l'URL qui sera utilisé dans le frontend
  }

  public List<Parcel> getAllDamagedParcels() {
    return parcelRepository.findByDamageImageUrlIsNotNull();  // Fetch all damaged parcels
  }
}
