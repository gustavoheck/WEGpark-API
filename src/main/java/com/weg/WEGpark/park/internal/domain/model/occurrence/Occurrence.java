package com.weg.WEGpark.park.internal.domain.model.occurrence;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(nullable = false, name = "date_hour")
    private LocalDateTime dateHour;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String gate;

    @Enumerated(EnumType.STRING)
    @Column(name = "occurrence_type", insertable = false, updatable = false, nullable = false)
    private OccurrenceType userType;

    public Occurrence(String location, String gate, OccurrenceType userType) {
        this.location = location;
        this.gate = gate;
        this.userType = userType;
    }
}
