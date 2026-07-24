package com.weg.WEGpark.park.internal.domain.model.users;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "auth", name = "parkuser_vehicle")
public class VehicleUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinColumn(name = "id_parkuser", nullable = false)
    private ParkUser parkUser;

    @ManyToMany
    @JoinColumn(name = "id_vehicle", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private Boolean proprietary;

    @Column(nullable = false)
    private Boolean active;

    @ManyToMany
    @JoinTable (
            schema = "park",
            name = "vehicle_user_occurrence",
            joinColumns = @JoinColumn(name = "id_parkuser_vehicle"),
            inverseJoinColumns = @JoinColumn(name = "id_occurrence")
    )
    private Occurrence occurrences;

    public VehicleUser(ParkUser parkUser, Vehicle vehicle) {
        this.parkUser = parkUser;
        this.vehicle = vehicle;
        this.active = true;
    }
}
