package com.weg.WEGpark.park.internal.dto.vehicle.defaults;

import jakarta.validation.constraints.Size;

public record UpdateVehicleRequestDTO(
        @Size(min = 7, max = 7, message = "The plate of of a vehicle")
        String plate,

        String model,

        String brand,

        String color
) {
}
