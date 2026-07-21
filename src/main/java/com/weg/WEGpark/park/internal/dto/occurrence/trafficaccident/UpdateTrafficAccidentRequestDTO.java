package com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident;

import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateTrafficAccidentRequestDTO(

        CreateOccurrenceRequestDto defaults,

        LocalDateTime occurrenceDate,

        String victimName,

        String responsibleBossName,

        String responsibleFactory,

        String responsibleSection,

        String trafficOccurrenceType,

        String guardTestimony,

        String victimTestimony
) {
}
