package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.SendManyOccurrencesWarnEvent;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OccurrenceNotificationMapper {

    public SendOccurrenceNotificationEvent toNotification (List<VehicleUser> occurrenceVehicleUsers, Occurrence occurrence, String message) {
        return new SendOccurrenceNotificationEvent(
                occurrenceVehicleUsers
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getId())
                        .toList(),
                message,
                occurrence.getUuid(),
                occurrenceVehicleUsers
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getName())
                        .toList(),
                occurrenceVehicleUsers
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getEmail())
                        .toList()
        );
    }

    public SendManyOccurrencesWarnEvent toFiveOccurrenceNotification
            (List<VehicleUser> occurrenceVehicleUsers, String message) {
        return new SendManyOccurrencesWarnEvent(
                occurrenceVehicleUsers
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getId())
                        .toList(),
                occurrenceVehicleUsers
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getName())
                        .toList(),
                occurrenceVehicleUsers
                        .stream()
                        .map(vehicleUser -> vehicleUser.getParkUser().getEmail())
                        .toList(),
                message
        );
    }
}
