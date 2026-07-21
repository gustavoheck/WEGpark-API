package com.weg.WEGpark.park.internal.controller.occurrence;

import com.weg.WEGpark.park.internal.app.occurrence.service.IllegalParkingService;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.GetIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.UpdateIllegalParkingRequestDTO;
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
@RequestMapping("/occurrence/illegal-parking")
public class IllegalParkingController {

    private final IllegalParkingService illegalParkingService;

    @PostMapping
    public ResponseEntity<CreateIllegalParkingResponseDTO> registerIllegalParking (@Valid @RequestBody CreateIllegalParkingRequestDTO request) {
        CreateIllegalParkingResponseDTO response = illegalParkingService.registerIllegalParkingOccurrence(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(response.uuid())
                .toUri();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<GetIllegalParkingResponseDTO> updateIllegalParking (
            @Valid @RequestBody
            UpdateIllegalParkingRequestDTO request,
            @PathVariable
            UUID uuid
    ) {
        GetIllegalParkingResponseDTO response = illegalParkingService.updateIllegalParking(uuid, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
