package com.weg.WEGpark.park.internal.domain.model.users;

import com.weg.WEGpark.park.internal.domain.enums.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "parkuser", schema = "park")
public class ParkUser {

    @Id
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String name;

    @Column(name = "user_type")
    private ParkUserType userType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "parkuser_vehicle",
            schema = "park",
            joinColumns = @JoinColumn(name = "id_parkuser"),
            inverseJoinColumns = @JoinColumn(name = "id_vehicle")
    )
    private List<Vehicle> vehicles;

    public ParkUser(String email, String telephone, String name, ParkUserType userType) {
        this.email = email;
        this.telephone = telephone;
        this.name = name;
        this.userType = userType;
    }
}
