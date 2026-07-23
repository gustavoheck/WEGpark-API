package com.weg.WEGpark.park;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;

import java.util.UUID;

public record AssociateToVehicleEvent(

        UUID uuid,

        String name,

        Vehicle vehicle
) {
}
