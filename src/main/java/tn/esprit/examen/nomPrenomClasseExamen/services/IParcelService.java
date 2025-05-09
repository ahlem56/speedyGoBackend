package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Status;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IParcelService {
  //    Parcel createParcel(Parcel parcel);
  Parcel createParcel(Parcel parcel, Integer userId);
  Parcel assignParcelToDriver(Integer parcelId, Integer driverId);

  //  Parcel createParcelWithAssignment(Parcel parcel, Integer driverId, Integer userId);
  Parcel getParcelById(Integer id);
  List<Parcel> getAllParcels();
  Parcel updateParcel(Integer id, Parcel parcelDetails);
   List<Parcel> getParcelsByDriver(Long driverId) ;
    void deleteParcel(Integer id);
  List<Parcel> getParcelsForUser(Integer userId);
  public List<Parcel> getParcelsAfterDate(Date date) ;
  public List<Parcel> getParcelsBeforeDate(Date date) ;

  float determineParcelPrice(double weight);
  // Méthodes pour récupérer les statistiques
  long getDeliveredParcelsByDay(Date date);

  long getDeliveredParcelsByWeek(Date date);

  long getDeliveredParcelsByMonth(Date date);
  Parcel markAsShipped(Long parcelId) throws Exception;
  void markAsDelivered(Long parcelId) throws Exception;
  List<Parcel> archiveDeliveredParcels();
  List<Parcel> getParcelsByStatus(Status status);  // Méthode pour récupérer les colis par statut
  //Map<String, Float> calculateYearlyRevenue();  // Méthode pour obtenir le revenu de chaque mois

  public Parcel findParcelById(Integer parcelId) ;

  Map<String, Float> calculateYearlyRevenue();

  public Parcel save(Parcel parcel) ;
  public List<Parcel> searchParcelsForDriver(Long driverId, String departure, String destination) ;

  //DAMAGED PARCEL
  public String saveDamageImage(Integer parcelId, MultipartFile image, String description) throws IOException ;
  public List<Parcel> getAllDamagedParcels() ;
  long getTotalParcels(); // Nouvelle méthode ajoutée

  //pdf
  byte[] generateParcelPdf(Integer parcelId);
  double getDamagedParcelsPercentage();

}
