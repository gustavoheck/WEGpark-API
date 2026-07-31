package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.SendManyOccurrencesWarnEvent;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OccurrenceNotificationMapper {

    public SendOccurrenceNotificationEvent toNotification (Occurrence occurrence, String message) {
        return new SendOccurrenceNotificationEvent(
                occurrence.getVehicleUsers()
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getId())
                        .toList(),
                message,
                occurrence.getUuid(),
                occurrence.getVehicleUsers()
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getName())
                        .toList(),
                occurrence.getVehicleUsers()
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getEmail())
                        .toList()
        );
    }

    public SendManyOccurrencesWarnEvent toFiveOccurrenceNotification
            (Occurrence occurrence, String message) {
        return new SendManyOccurrencesWarnEvent(
                occurrence.getVehicleUsers()
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getId())
                        .toList(),
                occurrence.getVehicleUsers()
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getName())
                        .toList(),
                occurrence.getVehicleUsers()
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getEmail())
                        .toList(),
                message
        );
    }
}
