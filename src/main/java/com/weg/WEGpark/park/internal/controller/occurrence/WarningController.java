package com.weg.WEGpark.park.internal.controller.occurrence;

import com.weg.WEGpark.park.internal.app.occurrence.service.WarningService;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.GetTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.UpdateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.GetWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.UpdateWarningRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/occurrence/warning")
public class WarningController {

    private final WarningService warningService;

    @PostMapping
    public ResponseEntity<CreateWarningResponseDTO> registerWarning (@Valid @RequestBody CreateWarningRequestDTO request) {
        CreateWarningResponseDTO response = warningService.registerWarningOccurrence(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(response.uuid())
                .toUri();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<GetWarningResponseDTO> updateWarning (
            @Valid @RequestBody
            UpdateWarningRequestDTO request,
            @PathVariable
            UUID uuid
    ) {
        GetWarningResponseDTO response = warningService.updateWarning(uuid, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
