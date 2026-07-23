package com.weg.WEGpark.notification.internal.app.service.notification;

import com.weg.WEGpark.notification.internal.app.service.mapper.EventMapper;
import com.weg.WEGpark.notification.internal.domain.entities.VehicleAssociationNotification;
import com.weg.WEGpark.notification.internal.infra.repository.NotificationRepository;
import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreateNotificationService {

    private final EventMapper eventMapper;
    private final NotificationRepository notificationRepository;

    public void CreateAssociationNotification (AssociateToVehicleNotificationEvent event) {
        VehicleAssociationNotification notification = eventMapper.toNotification(event);
        notificationRepository.save(notification);
    }
}
