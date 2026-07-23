package com.weg.WEGpark.park.internal.dto.vehicle.association;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssociationNotificationRequestDTO(

        @NotBlank(message = "The plate of vehicle can not be null")
        @Size(min = 7, max = 7, message = "The plate of of a vehicle")
        String plate
) {
}
