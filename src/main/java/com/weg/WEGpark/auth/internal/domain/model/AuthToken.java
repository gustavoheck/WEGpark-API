package com.weg.WEGpark.auth.internal.domain.model;

import com.weg.WEGpark.auth.internal.domain.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(schema = "auth", name = "auth_token")
public class AuthToken {

    @Id
    @Generated(event = EventType.INSERT)
    @Column(updatable = false, insertable = false)
    private UUID token;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "token_type")
    private TokenType tokenType;

    @Column(nullable = false)
    private Boolean used;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn(name = "id_target_user", nullable = false)
    private User targetUser;

    public AuthToken(TokenType tokenType, User targetUser) {
        this.tokenType = tokenType;
        this.used = false;
        this.expirationTime = LocalDateTime.now().plusMinutes(15);
        this.targetUser = targetUser;
    }
}
