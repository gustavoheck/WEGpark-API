package com.weg.WEGpark.notification.internal.app.notification.service;

import com.weg.WEGpark.auth.GetAuthUserIdEvent;
import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.notification.internal.app.notification.exception.InvalidNotificationException;
import com.weg.WEGpark.notification.internal.app.notification.mapper.NotificationEventMapper;
import com.weg.WEGpark.notification.internal.domain.entities.Notification;
import com.weg.WEGpark.notification.internal.domain.entities.VehicleAssociationNotification;
import com.weg.WEGpark.notification.internal.domain.enums.NotificationType;
import com.weg.WEGpark.notification.internal.infra.repository.NotificationRepository;
import com.weg.WEGpark.park.*;
import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {
    private NotificationEventMapper mapper;
    private NotificationRepository repository;
    private ApplicationEventPublisher publisher;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        mapper = mock(NotificationEventMapper.class);
        repository = mock(NotificationRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        service = new NotificationService(mapper, repository, publisher);
    }

    @Test
    void createsAssociationNotificationWithGeneratedMessage() {
        AssociateToVehicleNotificationEvent event = new AssociateToVehicleNotificationEvent(1L, 2L, "User", 3L, "Brand", "Model");
        VehicleAssociationNotification notification = new VehicleAssociationNotification(1L, 3L, 2L);
        when(mapper.toNotification(event)).thenReturn(notification);

        service.createAssociationNotification(event);

        assertTrue(notification.getMessage().contains("User"));
        verify(repository).save(notification);
    }

    @Test
    void createsOccurrenceNotificationsOnlyForAlignedEventLists() {
        SendOccurrenceNotificationEvent valid = new SendOccurrenceNotificationEvent(List.of(1L, 2L), "message", UUID.randomUUID(), List.of("A", "B"), List.of("a@weg.net", "b@weg.net"));
        service.createNewOccurrenceNotification(valid);
        verify(repository, times(2)).save(any());

        SendOccurrenceNotificationEvent invalid = new SendOccurrenceNotificationEvent(List.of(1L), "message", UUID.randomUUID(), List.of("A", "B"), List.of("a@weg.net", "b@weg.net"));
        assertThrows(InvalidNotificationException.class, () -> service.createNewOccurrenceNotification(invalid));
    }

    @Test
    void createsFiveOccurrenceNotificationsOnlyForAlignedEventLists() {
        SendManyOccurrencesWarnEvent valid = new SendManyOccurrencesWarnEvent(List.of(1L), List.of("A"), List.of("a@weg.net"), "message");
        service.createNewFiveOccurrenceNotification(valid);
        verify(repository).save(any());

        SendManyOccurrencesWarnEvent invalid = new SendManyOccurrencesWarnEvent(List.of(1L), List.of("A", "B"), List.of("a@weg.net", "b@weg.net"), "message");
        assertThrows(InvalidNotificationException.class, () -> service.createNewFiveOccurrenceNotification(invalid));
    }

    @Test
    void returnsAssociationDataAndRejectsWrongOwnerOrReuse() {
        UUID uuid = UUID.randomUUID();
        UUID ownerUuid = UUID.randomUUID();
        VehicleAssociationNotification notification = new VehicleAssociationNotification(1L, 2L, 3L);
        doAnswer(invocation -> {
            GetAuthUserIdEvent event = invocation.getArgument(0);
            event.eventResponse().complete(1L);
            return null;
        }).when(publisher).publishEvent(any(GetAuthUserIdEvent.class));
        when(repository.findAssociationForUpdate(uuid, 1L)).thenReturn(Optional.of(notification));
        CompletableFuture<FindAssociationNotificationResponse> future = new CompletableFuture<>();

        service.findAssociationNotification(new FindAssociationNotificationEvent(future, uuid, ownerUuid));

        assertEquals(new FindAssociationNotificationResponse(3L, 2L), future.join());
        assertTrue(notification.getUsed());
        verify(repository).save(notification);

        assertThrows(NotFoundException.class, () ->
                service.findAssociationNotification(new FindAssociationNotificationEvent(
                        new CompletableFuture<>(), uuid, ownerUuid
                ))
        );

        when(repository.findAssociationForUpdate(uuid, 1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                service.findAssociationNotification(new FindAssociationNotificationEvent(
                        new CompletableFuture<>(), uuid, ownerUuid
                ))
        );
    }

    @Test
    void returnsPagedNotificationsForParkUser() {
        UUID userUuid = UUID.randomUUID();
        JWTUserData userData = new JWTUserData(userUuid, "park@weg.net", List.of(RolesType.ROLE_PARK.name()), "Park");
        var pageable = PageRequest.of(0, 5);
        Notification notification = new Notification(10L, NotificationType.OCCURRENCE, "message");
        notification.setUuid(UUID.randomUUID());
        notification.setNotificationTime(LocalDateTime.now());

        doAnswer(invocation -> {
            GetAuthUserIdEvent event = invocation.getArgument(0);
            event.eventResponse().complete(10L);
            return null;
        }).when(publisher).publishEvent(any(GetAuthUserIdEvent.class));
        when(repository.findAllByIdNotificatedUser(10L, pageable))
                .thenReturn(new PageImpl<>(List.of(notification), pageable, 1));

        var response = service.findMyNotifications(userData, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(notification.getUuid(), response.getContent().getFirst().uuid());
        assertEquals(notification.getMessage(), response.getContent().getFirst().message());
    }

    @Test
    void deletesOnlyRhUserNotification() {
        UUID userUuid = UUID.randomUUID();
        UUID notificationUuid = UUID.randomUUID();
        JWTUserData userData = new JWTUserData(userUuid, "rh@weg.net", List.of(RolesType.ROLE_RH.name()), "RH");
        Notification notification = new Notification(20L, NotificationType.FIVE_OCCURRENCE, "message");

        doAnswer(invocation -> {
            GetAuthUserIdEvent event = invocation.getArgument(0);
            event.eventResponse().complete(20L);
            return null;
        }).when(publisher).publishEvent(any(GetAuthUserIdEvent.class));
        when(repository.findByUuidAndIdNotificatedUser(notificationUuid, 20L))
                .thenReturn(Optional.of(notification));

        service.deleteNotification(notificationUuid, userData);

        verify(repository).delete(notification);
    }

    @Test
    void rejectsNotificationFromAnotherUser() {
        UUID notificationUuid = UUID.randomUUID();
        JWTUserData guard = new JWTUserData(UUID.randomUUID(), "guard@weg.net", List.of(RolesType.ROLE_GUARD.name()), "Guard");
        doAnswer(invocation -> {
            GetAuthUserIdEvent event = invocation.getArgument(0);
            event.eventResponse().complete(30L);
            return null;
        }).when(publisher).publishEvent(any(GetAuthUserIdEvent.class));
        when(repository.findByUuidAndIdNotificatedUser(notificationUuid, 30L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.deleteNotification(notificationUuid, guard));
        verify(repository, never()).delete(any(Notification.class));
    }
}
