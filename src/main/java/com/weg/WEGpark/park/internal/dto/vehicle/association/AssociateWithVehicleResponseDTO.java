package com.weg.WEGpark.park.internal.dto.vehicle.association;

import java.util.UUID;

public record AssociateWithVehicleResponseDTO(
        UUID uuid,

        String email,

        String name
) {
}
