package com.weg.WEGpark.notification.internal.app.service.notification;

import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.notification.internal.app.service.mapper.EventMapper;
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

    private final EventMapper eventMapper;
    private final NotificationRepository notificationRepository;

    public void CreateAssociationNotification (AssociateToVehicleNotificationEvent event) {
        VehicleAssociationNotification notification = eventMapper.toNotification(event);
        notificationRepository.save(notification);
    }

    public void findAssociationNotification (FindAssociationNotificationEvent event) {
        VehicleAssociationNotification notification = (VehicleAssociationNotification) notificationRepository.findById(event.idNotification())
                .orElseThrow(() -> new NotFoundException("Any notification was found to do this association"));

        event.eventResponse().complete(new FindAssociationNotificationResponse(
                notification.getIdUserToAssociate(),
                notification.getIdVehicleToAssociate()
        ));
    }
}
