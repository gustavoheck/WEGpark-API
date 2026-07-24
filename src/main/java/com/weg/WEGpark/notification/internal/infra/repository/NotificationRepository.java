package com.weg.WEGpark.notification.internal.infra.repository;

import com.weg.WEGpark.notification.internal.domain.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByUuid (UUID uuid);
}
