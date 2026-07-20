package com.weg.WEGpark.park.internal.dto.occurrence.defaults;

import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.GetIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.GetTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.GetWarningResponseDTO;

import java.util.List;

public record GetOccurrenceResponseDTO (

        List<GetIllegalParkingResponseDTO> illegalParking,

        List<GetTrafficAccidentResponseDTO> trafficAccident,

        List<GetWarningResponseDTO> warning
) {
}
