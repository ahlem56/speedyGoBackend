package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Parcel;
import tn.esprit.examen.nomPrenomClasseExamen.entities.SimpleUser;

import java.util.Date;
import java.util.List;

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
  }
