package com.weg.WEGpark.notification.internal.app.notification.service;

import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.notification.internal.app.notification.mapper.NotificationEventMapper;
import com.weg.WEGpark.notification.internal.domain.entities.VehicleAssociationNotification;
import com.weg.WEGpark.notification.internal.infra.repository.NotificationRepository;
import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import com.weg.WEGpark.park.FindAssociationNotificationEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationEventMapper notificationEventMapper;
    private final NotificationRepository notificationRepository;

    public void CreateAssociationNotification (AssociateToVehicleNotificationEvent event) {
        VehicleAssociationNotification notification = notificationEventMapper.toNotification(event);
        notification.setMessage("Do you want to permit the user %s associate with your vehicle %s %s"
                .formatted(event.userToAssociateName(), event.vehicleBrand(), event.vehicleModel()));
        notificationRepository.save(notification);
    }

    public void findAssociationNotification (FindAssociationNotificationEvent event) {
        VehicleAssociationNotification notification = (VehicleAssociationNotification) notificationRepository.findByUuid(event.uuidNotification())
                .orElseThrow(() -> new NotFoundException("Any notification was found to do this association"));

        event.eventResponse().complete(new FindAssociationNotificationResponse(
                notification.getIdUserToAssociate(),
                notification.getIdVehicleToAssociate()
        ));
    }
}
