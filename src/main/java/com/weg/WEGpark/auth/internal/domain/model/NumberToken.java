package com.weg.WEGpark.auth.internal.domain.model;

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
@Table(schema = "auth", name = "number_token")
public class NumberToken {

    @Id
    @Generated(event = EventType.INSERT)
    @Column(updatable = false, insertable = false, name = "token")
    private UUID identificationToken;

    @Column(nullable = false)
    private String digits;

    @Column(nullable = false)
    private Integer tries;

    @Column(nullable = false)
    private Boolean used;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn(name = "id_target_user", nullable = false)
    private User targetUser;

    public NumberToken(String digits, User targetUser) {
        this.digits = digits;
        this.tries = 0;
        this.used = false;
        this.expirationTime = LocalDateTime.now().plusMinutes(15);
        this.targetUser = targetUser;
    }
}
