package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "simple_user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SimpleUser extends User {
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
    private Integer carpolingDoneSuser;

    @OneToMany(mappedBy = "simpleUserOffer", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("simpleUserOffer")
    private Set<Carpool> carpoolOffered = new LinkedHashSet<>();//offered carpools

    @ManyToMany(mappedBy = "simpleUserJoin", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("simpleUserJoin")
    private Set<Carpool> carpoolJoined = new LinkedHashSet<>();//joined carpools

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SimpleUser_events",
            joinColumns = @JoinColumn(name = "simpleUser_user_id"),
            inverseJoinColumns = @JoinColumn(name = "events_eventId"))
    @JsonIgnoreProperties("simpleUsers")
    private Set<Event> events = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partners_partner_id")
    @JsonBackReference
    private Partners partners;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_subscription_id")
    @JsonIgnoreProperties("simpleUsers")
    private Subscription subscription;

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("simpleUser")
    private Set<Complaints> complaints = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("simpleUser")
    private Set<Parcel> parcels = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("simpleUser")
    private Set<Notification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("simpleUser")
    private Set<Chat> chats = new LinkedHashSet<>();
    @Column(name = "role") // Add role field
    private String role;

    @OneToMany(mappedBy = "simpleUser", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("simpleUser")
    private Set<Trip> trips;
}

