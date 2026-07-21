package com.weg.WEGpark.park.internal.dto.vehicle.defaults;

import java.util.UUID;

public record CreateVehicleResponseDTO (

        UUID uuid,

        String plate,

        String model,

        String brand,

        String color
) {}
