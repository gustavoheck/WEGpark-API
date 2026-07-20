package com.weg.WEGpark.park.internal.dto.occurrence.defaults;

import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;

import java.util.List;

public record GetOccurrenceResponseDTO (

        List<CreateIllegalParkingResponseDTO> illegalParking,

        List<CreateTrafficAccidentResponseDTO> trafficAccident,

        List<CreateWarningResponseDTO> warning
) {
}
