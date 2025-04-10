package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Subscription type is required")
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    @NotNull(message = "Subscription price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Float subscriptionPrice;

    @NotNull(message = "Description is required")
    @Size(min = 4, message = "Description must have at least 4 characters")
    private String subscriptionDescription;

    @OneToMany(mappedBy = "subscription", orphanRemoval = true)
    private Set<SimpleUser> simpleUsers = new LinkedHashSet<>();
}
