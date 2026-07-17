package com.weg.WEGpark.park.internal.dto.vehicle;

public record FilterVehicleRequestDTO(

        String plate,

        String model,

        String brand,

        String color,

        String userName
) {
}
