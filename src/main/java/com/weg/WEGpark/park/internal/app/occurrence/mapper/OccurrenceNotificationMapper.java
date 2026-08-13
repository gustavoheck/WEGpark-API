package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.SendManyOccurrencesWarnEvent;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OccurrenceNotificationMapper {

    default SendOccurrenceNotificationEvent toNotification(
            List<VehicleUser> vehicleUsers,
            Occurrence occurrence,
            String message
    ) {
        if (vehicleUsers == null || occurrence == null) {
            return null;
        }

        return new SendOccurrenceNotificationEvent(
                extractIds(vehicleUsers),
                message,
                occurrence.getUuid(),
                extractNames(vehicleUsers),
                extractEmails(vehicleUsers)
        );
    }

    default SendManyOccurrencesWarnEvent toFiveOccurrenceNotification(
            List<VehicleUser> vehicleUsers,
            String message
    ) {
        if (vehicleUsers == null) {
            return null;
        }

        return new SendManyOccurrencesWarnEvent(
                extractIds(vehicleUsers),
                extractNames(vehicleUsers),
                extractEmails(vehicleUsers),
                message
        );
    }

    default List<Long> extractIds(List<VehicleUser> users) {
        if (users == null) return Collections.emptyList();

        return users.stream()
                .map(vu -> vu.getParkUser().getId())
                .toList();
    }

    default List<String> extractNames(List<VehicleUser> users) {
        if (users == null) return Collections.emptyList();
        return users.stream()
                .map(vu -> vu.getParkUser().getName())
                .toList();
    }

    default List<String> extractEmails(List<VehicleUser> users) {
        if (users == null) return Collections.emptyList();
        return users.stream()
                .map(vu -> vu.getParkUser().getEmail())
                .toList();
    }
}
