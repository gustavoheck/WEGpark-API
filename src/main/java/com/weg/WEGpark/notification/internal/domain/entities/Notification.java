package com.weg.WEGpark.notification.internal.domain.entities;

import com.weg.WEGpark.notification.internal.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorColumn(name = "notification_type")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(schema = "notification", name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(unique = true, nullable = false, insertable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false, name = "id_notificated_user")
    private Long idNotificatedUser;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false, name = "notification_type")
    private NotificationType notificationType;

    public Notification(Long idNotificatedUser, NotificationType notificationType) {
        this.idNotificatedUser = idNotificatedUser;
        this.notificationType = notificationType;
    }
}
