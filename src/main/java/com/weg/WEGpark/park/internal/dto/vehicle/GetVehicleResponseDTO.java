package com.weg.WEGpark.park.internal.dto.vehicle;

public record GetVehicleResponseDTO(

        String plate,

        String model,

        String brand,

        String color
) {
}
