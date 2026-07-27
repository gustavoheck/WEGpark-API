package com.weg.WEGpark.rh.internal.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "rh", name = "operation")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(unique = true, nullable = false, updatable = false, insertable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private UUID uuid_operated_user;

    public Operation(String operation, UUID uuid_operated_user) {
        this.operation = operation;
        this.uuid_operated_user = uuid_operated_user;
    }
}
