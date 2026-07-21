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
    private final IllegalParkingService illegalParkingService;
    private final TrafficAccidentService trafficAccidentService;
    private final WarningService warningService;

    @PostMapping("/traffic-accident")
    public ResponseEntity<CreateTrafficAccidentResponseDTO> registerTrafficAccident (@Valid @RequestBody CreateTrafficAccidentRequestDTO request) {
        CreateTrafficAccidentResponseDTO response = trafficAccidentService.registerTrafficAccidentOccurrence(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PostMapping("/illegal-parking")
    public ResponseEntity<CreateIllegalParkingResponseDTO> registerIllegalParking (@Valid @RequestBody CreateIllegalParkingRequestDTO request) {
        CreateIllegalParkingResponseDTO response = illegalParkingService.registerIllegalParkingOccurrence(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PostMapping("/warning")
    public ResponseEntity<CreateWarningResponseDTO> registerWarning (@Valid @RequestBody CreateWarningRequestDTO request) {
        CreateWarningResponseDTO response = warningService.registerWarningOccurrence(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<GetOccurrenceResponseDTO> findOccurrences(FilterOccurrenceRequestDTO filter) {

        GetOccurrenceResponseDTO response = occurrenceService.findAllOccurrences(filter);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
