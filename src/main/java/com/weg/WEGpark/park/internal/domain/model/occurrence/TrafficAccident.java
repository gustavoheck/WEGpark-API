package com.weg.WEGpark.park.internal.domain.model.occurrence;

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
@DiscriminatorValue("TRAFFIC_ACCIDENT")
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "traffic_accident", schema = "park")
public class TrafficAccident {

    @Column(name = "occurrence_date", nullable = false)
    private LocalDateTime occurrenceDate;

    @Column(name = "victim_name", nullable = false)
    private String victimName;

    @Column(name = "responsible_boss_name")
    private String responsibleBossName;

    @Column(name = "responsible_factory")
    private String responsibleFactory;

    @Column(name = "responsible_section")
    private String responsibleSection;

    @Column(name = "traffic_occurrence_type", nullable = false)
    private String trafficOccurrenceType;

    @Column(name = "guard_testimony", nullable = false)
    private String guardTestimony;

    @Column(name = "victim_testimony", nullable = false)
    private String victimTestimony;
}
