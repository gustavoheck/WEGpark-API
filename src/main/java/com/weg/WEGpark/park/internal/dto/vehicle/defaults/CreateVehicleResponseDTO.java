package com.weg.WEGpark.park.internal.dto.vehicle.defaults;

public record CreateVehicleResponseDTO (
        Long id,

        String plate,

        String model,

        String brand,

        String color
) {}
