package com.weg.WEGpark.rh.internal.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "rh", name = "rh")
public class Rh {

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

    @Column(nullable = false, name = "badge_number")
    private String badgeNumber;

    @OneToMany(mappedBy = "rh", fetch = FetchType.LAZY)
    private List<Operation> operations;

    public Rh(Long id, UUID uuid, String email, String telephone, String name, String badgeNumber) {
        this.id = id;
        this.uuid = uuid;
        this.email = email;
        this.telephone = telephone;
        this.name = name;
        this.badgeNumber = badgeNumber;
    }
}
