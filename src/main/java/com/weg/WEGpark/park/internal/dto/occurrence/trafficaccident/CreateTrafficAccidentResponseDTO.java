package com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident;

import com.weg.WEGpark.park.internal.dto.occurrence.defaults.OccurrenceResponseDto;

import java.time.LocalDateTime;

public record CreateTrafficAccidentResponseDTO(
        Long id,

        OccurrenceResponseDto defaults,

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
