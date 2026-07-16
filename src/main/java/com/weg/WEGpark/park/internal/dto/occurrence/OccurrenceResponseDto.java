package com.weg.WEGpark.park.internal.dto.occurrence;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record OccurrenceResponseDto(

        Long id,

        LocalDateTime dateHour,

        String location,

        String gate
) {
}
