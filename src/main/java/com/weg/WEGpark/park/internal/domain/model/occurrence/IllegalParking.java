package com.weg.WEGpark.park.internal.domain.model.occurrence;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
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
@DiscriminatorValue("ILLEGAL_PARKING")
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "illegal_parking", schema = "park")
public class IllegalParking extends Occurrence {

    @Enumerated(EnumType.STRING)
    @Column(name = "parking_space_type", nullable = false)
    private ParkingSpaceType parkingSpaceType;

    @Column(nullable = false)
    private String description;

    public IllegalParking(String location, String gate, ParkingSpaceType parkingSpaceType, String description) {
        super(location, gate);
        this.parkingSpaceType = parkingSpaceType;
        this.description = description;
    }
}
