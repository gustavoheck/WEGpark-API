package com.weg.WEGpark.park.internal.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "park", name = "occurrence")
@Getter
@Setter
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

    public Occurrence(String location, String gate) {
        this.location = location;
        this.gate = gate;
    }
}
