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
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetIllegalParkingResponseDTO> updateIllegalParking (
            @Valid @RequestBody
            UpdateIllegalParkingRequestDTO request,
            @PathVariable
            Long id
    ) {
        GetIllegalParkingResponseDTO response = illegalParkingService.updateIllegalParking(id, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
