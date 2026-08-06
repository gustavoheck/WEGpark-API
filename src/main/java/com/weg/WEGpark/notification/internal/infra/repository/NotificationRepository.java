package com.weg.WEGpark.notification.internal.infra.repository;

import com.weg.WEGpark.notification.internal.domain.entities.Notification;
import com.weg.WEGpark.notification.internal.domain.entities.VehicleAssociationNotification;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByUuid (UUID uuid);

    Page<Notification> findAllByIdNotificatedUser(Long idNotificatedUser, Pageable pageable);

    Optional<Notification> findByUuidAndIdNotificatedUser(UUID uuid, Long idNotificatedUser);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT notification
            FROM VehicleAssociationNotification notification
            WHERE notification.uuid = :uuid
              AND notification.idNotificatedUser = :idNotificatedUser
            """)
    Optional<VehicleAssociationNotification> findAssociationForUpdate(
            @Param("uuid") UUID uuid,
            @Param("idNotificatedUser") Long idNotificatedUser
    );
}
