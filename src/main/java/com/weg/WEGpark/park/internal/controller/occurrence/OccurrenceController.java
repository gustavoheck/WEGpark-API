package com.weg.WEGpark.park.internal.controller.occurrence;

import com.weg.WEGpark.park.internal.app.occurrence.service.IllegalParkingService;
import com.weg.WEGpark.park.internal.app.occurrence.service.OccurrenceService;
import com.weg.WEGpark.park.internal.app.occurrence.service.TrafficAccidentService;
import com.weg.WEGpark.park.internal.app.occurrence.service.WarningService;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.GetOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.filter.FilterOccurrenceRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/occurrence")
public class OccurrenceController {

    private final OccurrenceService occurrenceService;
    private final WarningService warningService;

    @GetMapping
    public ResponseEntity<GetOccurrenceResponseDTO> findOccurrences(FilterOccurrenceRequestDTO filter) {

        GetOccurrenceResponseDTO response = occurrenceService.findAllOccurrences(filter);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
