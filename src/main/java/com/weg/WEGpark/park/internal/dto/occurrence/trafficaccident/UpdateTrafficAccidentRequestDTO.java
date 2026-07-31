package com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident;

import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDTO;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

public record UpdateTrafficAccidentRequestDTO(

        @Valid
        CreateOccurrenceRequestDTO defaults,

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
