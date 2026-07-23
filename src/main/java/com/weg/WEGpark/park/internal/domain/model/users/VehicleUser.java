package com.weg.WEGpark.park.internal.domain.model.users;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
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

    public VehicleUser(Boolean proprietary, Boolean active) {
        this.proprietary = proprietary;
        this.active = active;
    }
}
