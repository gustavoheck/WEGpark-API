package com.weg.WEGpark.notification.internal.domain.entities;

import com.weg.WEGpark.notification.internal.domain.enums.EntityNotificationType;
import com.weg.WEGpark.notification.internal.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorColumn(name = "entity_notification_type")
@DiscriminatorValue("null")
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

    @Column(nullable = false, name = "notification_time")
    private LocalDateTime notificationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_notification_type", insertable = false, updatable = false)
    private EntityNotificationType entityNotificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    public Notification(Long idNotificatedUser, NotificationType notificationType) {
        this.idNotificatedUser = idNotificatedUser;
        this.notificationType = notificationType;
        this.notificationTime = LocalDateTime.now();
    }

    public Notification(Long idNotificatedUser, NotificationType notificationType, String message) {
        this.idNotificatedUser = idNotificatedUser;
        this.notificationType = notificationType;
        this.message = message;
        this.notificationTime = LocalDateTime.now();
    }
}
