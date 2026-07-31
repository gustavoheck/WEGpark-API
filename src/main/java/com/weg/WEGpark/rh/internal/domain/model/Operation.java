package com.weg.WEGpark.rh.internal.domain.model;

import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
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

    @Column(nullable = false, name = "date_hour")
    private LocalDateTime dateHour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "operation")
    private OperationType operationType;

    @Column(nullable = false, name = "id_operated_user")
    private Long idOperatedUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rh", nullable = false)
    private Rh rh;

    public Operation(OperationType operationType, Long idOperatedUser) {
        this.operationType = operationType;
        this.idOperatedUser = idOperatedUser;
        this.dateHour = LocalDateTime.now();
    }
}
