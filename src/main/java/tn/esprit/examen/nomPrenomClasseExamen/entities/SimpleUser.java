package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUser extends User{
    private String phoneNumber;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String bio;
    private String website;
    private String socialMedia;
    private String interests;
    private String skills;
    private String education;
    private String experience;
    private String certifications;
    private String awards;
    private String languages;
    private String hobbies;
    private Integer partnerCodeSuser;
    private Boolean subscriptionSuser;
    private Integer carpolingDoneSuser = 0; // Nombre d'offres créées

    private Integer points=0 ; // Points initiaux
    private LocalDateTime lastActiveDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel = ActivityLevel.INACTIF;

    private LocalDate subscriptionStartDate;

    @Column(nullable = true)
    private Double averageRating; // Pourcentage de "Oui" ou null si aucune notation
    private Float discountedPrice;  // Store the user's discounted price

    @Column(name = "role") // Add role field
    private String role;

    @OneToMany(mappedBy = "simpleUserOffer")

    private Set<Carpool> carpoolOffered = new LinkedHashSet<>();//offered carpools

    @ManyToMany(mappedBy = "simpleUserJoin")

    private Set<Carpool> carpoolJoined = new LinkedHashSet<>();//joined carpools


    @ManyToMany
    @JoinTable(name = "SimpleUser_events",
            joinColumns = @JoinColumn(name = "simpleUser_user_id"),
            inverseJoinColumns = @JoinColumn(name = "events_eventId"))
    private Set<Event> events = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "partners_partner_id")
    @JsonBackReference
    private Partners partners;
    @ManyToOne
    @JoinColumn(name = "subscription_subscription_id") // This links SimpleUser to Subscription
    private Subscription subscription;


    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true)
    private Set<Complaints> complaints = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true)
    @JsonManagedReference(value = "simpleUser-parcels")
    private Set<Parcel> parcels = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true)
    private Set<Notification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true)
    private Set<Chat> chats = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser")  // Correct mapping
    private Set<Trip> trips;



}