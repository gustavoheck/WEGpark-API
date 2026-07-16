package com.weg.WEGpark.park.internal.dto.vehicle;

public record CreateVehicleResponseDTO (
        Long id,

        String plate,

        String model,

        String brand,

        String color
) {}
