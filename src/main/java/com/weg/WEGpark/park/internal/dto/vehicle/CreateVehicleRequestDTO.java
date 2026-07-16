package com.weg.WEGpark.park.internal.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVehicleRequestDTO(

        @NotBlank(message = "The plate of vehicle can not be null")
        @Size(min = 7, max = 7, message = "The plate of of a vehicle")
        String plate,

        @NotBlank(message = "The name of vehicle can not be null or blank")
        String model,

        @NotBlank(message = "The brand of the vehicle can not be null or blank")
        String brand,

        @NotBlank(message = "The color of the vehicle can not be null or blank")
        String color
) {
}
