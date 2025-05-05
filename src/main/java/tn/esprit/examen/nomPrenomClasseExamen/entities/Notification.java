package tn.esprit.examen.nomPrenomClasseExamen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;
    @Column(length = 2000)  // or whatever length you need
    private String notificationContent;
    private Date notificationDate;
    private String notificationStatus;

    @ManyToOne
    @JoinColumn(name = "simple_user_user_id")
    private SimpleUser simpleUser;

    @ManyToOne
    @JoinColumn(name = "driver_user_id")
    private Driver driver;

}

