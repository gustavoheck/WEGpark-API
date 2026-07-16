package com.weg.WEGpark.park.internal.domain;

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
@Table(schema = "park", name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String model;

    @Column(nullable = false)
    String brand;

    @Column(nullable = false)
    String color;

    public Vehicle(String model, String brand, String color) {
        this.model = model;
        this.brand = brand;
        this.color = color;
    }
}
