package com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident;

import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDto;

import java.time.LocalDateTime;

public record CreateTrafficAccidentResponseDTO(
        Long id,

        DefaultOccurrenceResponseDto defaults,

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
