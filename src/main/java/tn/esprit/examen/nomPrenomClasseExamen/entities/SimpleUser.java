package tn.esprit.examen.nomPrenomClasseExamen.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class SimpleUser extends User{
    private Integer partnerCodeSuser;
    private Boolean subscriptionSuser;
    private Integer carpolingDoneSuser;


    @OneToMany(mappedBy = "simpleUserOffer")

    private Set<Carpool> carpoolOffered = new LinkedHashSet<>();//offered carpools

    @ManyToMany(mappedBy = "simpleUserJoin")

    private Set<Carpool> carpoolJoined = new LinkedHashSet<>();//joined carpools


    @ManyToMany
    @JoinTable(name = "SimpleUser_events",
            joinColumns = @JoinColumn(name = "simpleUser_user_id"),
            inverseJoinColumns = @JoinColumn(name = "events_eventId"))
    private Set<Event> events = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "partners_partner_id")
    private Partners partners;

    @ManyToOne
    @JoinColumn(name = "subscription_subscription_id") // This links SimpleUser to Subscription
    private Subscription subscription;


    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true)
    private Set<Complaints> complaints = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true)
    private Set<Parcel> parcels = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true)
    private Set<Notification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser", orphanRemoval = true)
    private Set<Chat> chats = new LinkedHashSet<>();

    @OneToMany(mappedBy = "simpleUser")  // Correct mapping
    private Set<Trip> trips;

}

