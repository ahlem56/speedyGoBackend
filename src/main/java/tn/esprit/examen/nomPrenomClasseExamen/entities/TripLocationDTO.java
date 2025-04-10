package tn.esprit.examen.nomPrenomClasseExamen.entities;

public class TripLocationDTO {
    private String location;  // Location name (departure or destination)
    private Double latitude;  // Latitude
    private Double longitude; // Longitude
    private Integer tripCount;  // Number of trips at this location

    // Constructor, Getters and Setters
    public TripLocationDTO(String location, Double latitude, Double longitude, Integer tripCount) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tripCount = tripCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getTripCount() {
        return tripCount;
    }

    public void setTripCount(Integer tripCount) {
        this.tripCount = tripCount;
    }
}
