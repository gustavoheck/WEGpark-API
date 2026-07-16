package com.weg.WEGpark.park.internal.controller.vehicle;

import com.weg.WEGpark.park.internal.app.vehicle.service.VehicleService;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<CreateVehicleResponseDTO> registerVehicle (@Valid @RequestBody CreateVehicleRequestDTO request) {
        CreateVehicleResponseDTO response = vehicleService.registerVehicle(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
