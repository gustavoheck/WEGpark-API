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
@DiscriminatorValue("COLLABORATOR")
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "collaborator", schema = "park")
public class Collaborator extends ParkUser {

    @Column(nullable = false, name = "badge_number")
    private String badgeNumber;

    @Column(nullable = false)
    private String location;

    public Collaborator(String email, String telephone, String name, ParkUserType userType, String badgeNumber, String location) {
        super(email, telephone, name, userType);
        this.badgeNumber = badgeNumber;
        this.location = location;
    }
}
