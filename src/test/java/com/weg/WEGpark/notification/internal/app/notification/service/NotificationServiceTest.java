package com.weg.WEGpark.notification.internal.app.notification.service;

import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.notification.internal.app.notification.exception.InvalidNotificationException;
import com.weg.WEGpark.notification.internal.app.notification.mapper.NotificationEventMapper;
import com.weg.WEGpark.notification.internal.domain.entities.VehicleAssociationNotification;
import com.weg.WEGpark.notification.internal.infra.repository.NotificationRepository;
import com.weg.WEGpark.park.*;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {
    private NotificationEventMapper mapper;
    private NotificationRepository repository;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        mapper = mock(NotificationEventMapper.class);
        repository = mock(NotificationRepository.class);
        service = new NotificationService(mapper, repository);
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
    void returnsAssociationDataOrNotFound() {
        UUID uuid = UUID.randomUUID();
        VehicleAssociationNotification notification = new VehicleAssociationNotification(1L, 2L, 3L);
        when(repository.findByUuid(uuid)).thenReturn(Optional.of(notification));
        CompletableFuture<FindAssociationNotificationResponse> future = new CompletableFuture<>();

        service.findAssociationNotification(new FindAssociationNotificationEvent(future, uuid));

        assertEquals(new FindAssociationNotificationResponse(3L, 2L), future.join());
        when(repository.findByUuid(uuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findAssociationNotification(new FindAssociationNotificationEvent(new CompletableFuture<>(), uuid)));
    }
}
