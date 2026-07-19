package com.weg.WEGpark.park.internal.controller.vehicle;

import com.weg.WEGpark.park.internal.app.vehicle.service.VehicleService;
import com.weg.WEGpark.park.internal.dto.vehicle.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<CreateVehicleResponseDTO> registerVehicle (@Valid @RequestBody CreateVehicleRequestDTO request) {
        CreateVehicleResponseDTO response = vehicleService.registerVehicle(request);

        // Need to change to ResponseEntity.create
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<GetVehicleResponseDTO>> findVehicles (FilterVehicleRequestDTO filter) {
        List<GetVehicleResponseDTO> filteredVehicles = vehicleService.findVehicle(filter);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(filteredVehicles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetVehicleResponseDTO> updateVehicle (
            @Valid @RequestBody
            UpdateVehicleRequestDTO request,
            @PathVariable
            Long id
    ) {
        GetVehicleResponseDTO response = vehicleService.updateVehicle(id, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
