package com.weg.WEGpark.park.internal.dto.vehicle.defaults;

public record GetVehicleResponseDTO(

        String plate,

        String model,

        String brand,

        String color
) {
}
