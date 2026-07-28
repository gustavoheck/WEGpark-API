package com.weg.WEGpark.park.internal.dto.vehicle.defaults;

import java.util.List;
import java.util.UUID;

public record UpdateVehicleResponseDTO(
        UUID uuid,

        String plate,

        String model,

        String brand,

        String color
) {
}
