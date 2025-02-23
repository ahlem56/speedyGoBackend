package tn.esprit.examen.nomPrenomClasseExamen.entities;

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
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer SubscriptionId;
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;
    private Float subscriptionPrice;
    private String subscriptionDescription;

    @OneToMany(mappedBy = "subscription", orphanRemoval = true)
    private Set<SimpleUser> simpleUsers = new LinkedHashSet<>();

}

