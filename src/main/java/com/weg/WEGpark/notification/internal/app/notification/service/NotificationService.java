package com.weg.WEGpark.notification.internal.app.notification.service;

import com.weg.WEGpark.auth.GetAuthUserIdEvent;
import com.weg.WEGpark.auth.shared.dto.JWTUserData;
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
import com.weg.WEGpark.park.SendManyOccurrencesWarnEvent;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        notification.setUsed(false);
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

    @Transactional
    public void findAssociationNotification(FindAssociationNotificationEvent event) {
        Long notificatedUserId = findNotificatedUserId(event.notificatedUserUuid());

        VehicleAssociationNotification notification = notificationRepository
                .findAssociationForUpdate(event.uuidNotification(), notificatedUserId)
                .orElseThrow(() -> new NotFoundException("Any notification was found to do this association"));

        if (Boolean.TRUE.equals(notification.getUsed())) {
            throw new NotFoundException("This association notification is no longer available");
        }

        notification.setUsed(true);
        notificationRepository.save(notification);

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
        return findNotificatedUserId(jwtUserData.uuid());
    }

    private Long findNotificatedUserId(UUID userUuid) {
        CompletableFuture<Long> eventResponse = new CompletableFuture<>();

        applicationEventPublisher.publishEvent(new GetAuthUserIdEvent(eventResponse, userUuid));

        return eventResponse.join();
    }


}
