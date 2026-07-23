package com.weg.WEGpark.park;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;

import java.util.UUID;

public record AssociateToVehicleNotificationEvent(

        Long idNotificatedUser,

        Long idUserToAssociate,

        Long idVehicleToAssociate
) {
}
