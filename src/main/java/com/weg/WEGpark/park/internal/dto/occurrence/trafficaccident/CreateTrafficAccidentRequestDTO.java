package com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident;

import com.weg.WEGpark.park.internal.dto.occurrence.defaults.OccurrenceRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateTrafficAccidentRequestDTO (
        OccurrenceRequestDto defaults,

        @NotNull(message = "The occurrence date can not be null")
        LocalDateTime occurrenceDate,

        @NotBlank(message = "The victim name can not be null or blank")
        String victimName,

        @NotBlank(message = "The responsible boss name can not be null or blank")
        String responsibleBossName,

        @NotBlank(message = "The responsible factory can not be null or blank")
        String responsibleFactory,

        @NotBlank(message = "The responsible section can not be null or blank")
        String responsibleSection,

        @NotBlank(message = "The traffic occurrence type can not be null or blank")
        String trafficOccurrenceType,

        @NotBlank(message = "The guard testimony can not be null or blank")
        String guardTestimony,

        @NotBlank(message = "The victim testimony can not be null or blank")
        String victimTestimony
) {
}
