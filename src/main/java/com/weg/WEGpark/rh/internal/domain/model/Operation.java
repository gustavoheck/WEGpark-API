package com.weg.WEGpark.rh.internal.domain.model;

import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
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
@Table(schema = "rh", name = "operation")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(unique = true, nullable = false, updatable = false, insertable = false)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "operation")
    private OperationType operationType;

    @Column(nullable = false)
    private UUID uuid_operated_user;

    @OneToMany(mappedBy = "operation", fetch = FetchType.LAZY)
    private List<Historic> historicList;

    public Operation(OperationType operationType, UUID uuid_operated_user) {
        this.operationType = operationType;
        this.uuid_operated_user = uuid_operated_user;
    }
}
