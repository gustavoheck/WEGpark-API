package com.weg.WEGpark.notification.internal.app.notification.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.notification.internal.app.notification.exception.InvalidNotificationException;
import com.weg.WEGpark.notification.internal.app.notification.mapper.NotificationEventMapper;
import com.weg.WEGpark.notification.internal.domain.entities.Notification;
import com.weg.WEGpark.notification.internal.domain.entities.VehicleAssociationNotification;
import com.weg.WEGpark.notification.internal.domain.enums.NotificationType;
import com.weg.WEGpark.notification.internal.dto.GetNotificationResponseDTO;
import com.weg.WEGpark.notification.internal.infra.repository.NotificationRepository;
import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import com.weg.WEGpark.park.FindAssociationNotificationEvent;
import com.weg.WEGpark.park.GetParkUserIdEvent;
import com.weg.WEGpark.park.SendManyOccurrencesWarnEvent;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import com.weg.WEGpark.rh.GetRhUserIdEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationEventMapper notificationEventMapper;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void createAssociationNotification (AssociateToVehicleNotificationEvent event) {
        VehicleAssociationNotification notification = notificationEventMapper.toNotification(event);
        notification.setMessage("Do you want to permit the user %s associate with your vehicle %s %s"
                .formatted(event.userToAssociateName(), event.vehicleBrand(), event.vehicleModel()));
        notificationRepository.save(notification);
    }

    @Transactional
    public void createNewOccurrenceNotification (SendOccurrenceNotificationEvent event) {
        if (event.notificatedUsersId().size() == event.userNames().size()) {
            for (int i = 0; i < event.notificatedUsersId().size(); i++) {
                Notification notification = new Notification(
                        event.notificatedUsersId().get(i),
                        NotificationType.OCCURRENCE,
                        event.defaultNotificationMessage()
                );
                notificationRepository.save(notification);
            }
        } else {
            throw new InvalidNotificationException("The notification event have different sizes for user and names");
        }
    }

    @Transactional
    public void createNewFiveOccurrenceNotification (SendManyOccurrencesWarnEvent event) {
        if (event.notificatedUsersName().size() == event.notificatedUsersId().size()) {
            for (int i = 0; i < event.notificatedUsersId().size(); i++) {
                Notification notification = new Notification(
                        event.notificatedUsersId().get(i),
                        NotificationType.FIVE_OCCURRENCE,
                        event.defaultNotificationMessage()
                );
                notificationRepository.save(notification);
            }
        } else {
            throw new InvalidNotificationException("The notification event have different sizes for user and names");
        }
    }

    public void findAssociationNotification (FindAssociationNotificationEvent event) {
        VehicleAssociationNotification notification = (VehicleAssociationNotification) notificationRepository.findByUuid(event.uuidNotification())
                .orElseThrow(() -> new NotFoundException("Any notification was found to do this association"));

        event.eventResponse().complete(new FindAssociationNotificationResponse(
                notification.getIdUserToAssociate(),
                notification.getIdVehicleToAssociate()
        ));
    }

    public Page<GetNotificationResponseDTO> findMyNotifications(JWTUserData jwtUserData, Pageable pageable) {
        Long notificatedUserId = findNotificatedUserId(jwtUserData);

        return notificationRepository.findAllByIdNotificatedUser(notificatedUserId, pageable)
                .map(notification -> new GetNotificationResponseDTO(
                        notification.getUuid(),
                        notification.getNotificationTime(),
                        notification.getNotificationType(),
                        notification.getMessage()
                ));
    }

    @Transactional
    public void deleteNotification(UUID uuid, JWTUserData jwtUserData) {
        Long notificatedUserId = findNotificatedUserId(jwtUserData);
        Notification notification = notificationRepository
                .findByUuidAndIdNotificatedUser(uuid, notificatedUserId)
                .orElseThrow(() -> new NotFoundException("Any notification was found by %s uuid".formatted(uuid)));

        notificationRepository.delete(notification);
    }

    private Long findNotificatedUserId(JWTUserData jwtUserData) {
        CompletableFuture<Long> eventResponse = new CompletableFuture<>();

        if (jwtUserData.roles().contains(RolesType.ROLE_RH.name())) {
            applicationEventPublisher.publishEvent(new GetRhUserIdEvent(eventResponse, jwtUserData.uuid()));
        } else if (jwtUserData.roles().contains(RolesType.ROLE_PARK.name())
                || jwtUserData.roles().contains(RolesType.ROLE_GUARD.name())) {
            applicationEventPublisher.publishEvent(new GetParkUserIdEvent(eventResponse, jwtUserData.uuid()));
        } else {
            throw new AccessDeniedException("Only Park, Guard or Rh users can access notifications");
        }

        return eventResponse.join();
    }


}
