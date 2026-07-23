package com.weg.WEGpark.park.internal.domain.model.occurrence;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "occurrence_type", discriminatorType = DiscriminatorType.STRING)
@Table(schema = "park", name = "occurrence")
public class Occurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(unique = true, nullable = false, updatable = false, insertable = false)
    private UUID uuid;

    @Column(nullable = false, name = "date_hour")
    private LocalDateTime dateHour;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String gate;

    @Enumerated(EnumType.STRING)
    @Column(name = "occurrence_type", insertable = false, updatable = false, nullable = false)
    private OccurrenceType occurrenceType;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "occurrences")
    private Guard guard;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "occurrences")
    private VehicleUser vehicleUser;

    public Occurrence(String location, String gate, OccurrenceType occurrenceType) {
        this.location = location;
        this.gate = gate;
        this.occurrenceType = occurrenceType;
    }
}
