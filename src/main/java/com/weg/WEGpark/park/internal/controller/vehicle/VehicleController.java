package com.weg.WEGpark.park.internal.controller.vehicle;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.internal.app.user.service.VehicleUserService;
import com.weg.WEGpark.park.internal.app.vehicle.service.VehicleService;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociateWithVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociationNotificationRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.CreateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.CreateVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.GetVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.UpdateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.filter.FilterVehicleRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleUserService vehicleUserService;

    @PostMapping
    public ResponseEntity<CreateVehicleResponseDTO> registerVehicle(
            @Valid @RequestBody CreateVehicleRequestDTO request,
            @AuthenticationPrincipal JWTUserData userData
    ) {
        CreateVehicleResponseDTO response = vehicleService.registerVehicle(request, userData);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(response.uuid())
                .toUri();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PostMapping("/associate/{uuidNotification}")
    public ResponseEntity<AssociateWithVehicleResponseDTO> associateVehicle (@PathVariable UUID uuidNotification) {
        AssociateWithVehicleResponseDTO response = vehicleService.associateToRegisteredVehicle(uuidNotification);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(response.uuid())
                .toUri();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PostMapping("/associate/notification")
    public ResponseEntity<Void> sendAssociationNotification (
            @Valid @RequestBody AssociationNotificationRequestDTO request,
            @AuthenticationPrincipal JWTUserData userData
    ) {
        vehicleService.SendNotificationForAssociate(request, userData);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/associate/disable/{vehicleUuid}")
    public ResponseEntity<Void> removeAssociation (@PathVariable UUID vehicleUuid) {
        vehicleUserService.disassociateVehicle(vehicleUuid);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GetVehicleResponseDTO>> findVehicles(FilterVehicleRequestDTO filter) {
        List<GetVehicleResponseDTO> filteredVehicles = vehicleService.findVehicle(filter);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(filteredVehicles);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<GetVehicleResponseDTO> updateVehicle(
            @Valid @RequestBody
            UpdateVehicleRequestDTO request,
            @PathVariable
            UUID uuid
    ) {
        GetVehicleResponseDTO response = vehicleService.updateVehicle(uuid, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}

