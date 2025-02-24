package tn.esprit.examen.nomPrenomClasseExamen.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.DriverRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.ParcelRepository;
import tn.esprit.examen.nomPrenomClasseExamen.repositories.SimpleUserRepository;

import java.util.Date;
import java.util.List;

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
//    @Override
//    public Parcel createParcel(Parcel parcel) {
//        return parcelRepository.save(parcel);
//    }

  @Override
  public Parcel createParcel(Parcel parcel, Integer userId) {
    // Vérifier si le SimpleUser existe
    SimpleUser user = simpleUserRepository.findById(userId)
      .orElseThrow(() -> new RuntimeException("SimpleUser not found with id: " + userId));

    // Associer le colis à l'utilisateur
    parcel.setSimpleUser(user);
    parcel.setParcelDate(new Date()); // Définir la date de création

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
}
