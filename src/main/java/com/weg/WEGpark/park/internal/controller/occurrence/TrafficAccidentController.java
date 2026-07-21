package com.weg.WEGpark.park.internal.controller.occurrence;

import com.weg.WEGpark.park.internal.app.occurrence.service.TrafficAccidentService;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.GetTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.UpdateTrafficAccidentRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/occurrence/traffic-accident")
public class TrafficAccidentController {

    private final TrafficAccidentService trafficAccidentService;

    @PostMapping
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

    @PutMapping("/{id}")
    public ResponseEntity<GetTrafficAccidentResponseDTO> updateTrafficAccident (
            @Valid @RequestBody
            UpdateTrafficAccidentRequestDTO request,
            @PathVariable
            Long id
    ) {
        GetTrafficAccidentResponseDTO response = trafficAccidentService.updateTrafficAccident(id, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
