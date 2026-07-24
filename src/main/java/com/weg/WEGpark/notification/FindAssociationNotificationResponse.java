package com.weg.WEGpark.notification;

public record FindAssociationNotificationResponse(
    Long idUserToAssociate,

    Long idVehicleToAssociate
) {
}
