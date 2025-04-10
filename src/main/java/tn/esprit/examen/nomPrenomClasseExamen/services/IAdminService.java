package tn.esprit.examen.nomPrenomClasseExamen.services;

import tn.esprit.examen.nomPrenomClasseExamen.entities.Admin;
import tn.esprit.examen.nomPrenomClasseExamen.entities.Driver;
import tn.esprit.examen.nomPrenomClasseExamen.entities.TripLocationDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IAdminService {
  Admin createAdmin(Admin admin);
  public long getTotalTrips();
  public List<TripLocationDTO> getTripsByLocation();
  public Map<String, BigDecimal> getMonthlyRevenue();

}
