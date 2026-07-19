package com.weg.WEGpark.park.internal.domain.model.users;

import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("GUARD")
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "guard", schema = "park")
public class Guard extends Collaborator{

    @Column(nullable = false)
    private String boss;

    public Guard(String email, String telephone, String name, ParkUserType userType, String badgeNumber, String location, String boss) {
        super(email, telephone, name, userType, badgeNumber, location);
        this.boss = boss;
    }
}
