package com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident;

import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceResponseDto;

import java.time.LocalDateTime;

public record GetTrafficAccidentResponseDTO(

        CreateOccurrenceResponseDto defaults,

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
