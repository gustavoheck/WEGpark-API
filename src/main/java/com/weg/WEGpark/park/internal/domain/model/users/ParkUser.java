package com.weg.WEGpark.park.internal.domain.model.users;

import com.weg.WEGpark.park.internal.domain.enums.ParkUserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public ParkUser(String email, String telephone, String name, ParkUserType userType) {
        this.email = email;
        this.telephone = telephone;
        this.name = name;
        this.userType = userType;
    }
}
