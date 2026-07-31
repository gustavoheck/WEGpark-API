package com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident;

import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTrafficAccidentResponseDTO(

        UUID uuid,

        DefaultOccurrenceResponseDTO defaults,

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
