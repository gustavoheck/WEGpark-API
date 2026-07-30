package com.weg.WEGpark.park.internal.controller.vehicle;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.internal.app.user.service.VehicleUserService;
import com.weg.WEGpark.park.internal.app.vehicle.service.VehicleService;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociateWithVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociationNotificationRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.*;
import com.weg.WEGpark.park.internal.dto.vehicle.filter.FilterVehicleRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<GetVehicleResponseDTO> registerVehicle(
            @Valid @RequestBody CreateVehicleRequestDTO request,
            @AuthenticationPrincipal JWTUserData userData
    ) {
        GetVehicleResponseDTO response = vehicleService.registerVehicle(request, userData);

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
    public ResponseEntity<Void> removeAssociation (
            @PathVariable UUID vehicleUuid,
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        vehicleUserService.disassociateVehicle(vehicleUuid, jwtUserData);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<GetVehicleResponseDTO>> findVehicles(
            FilterVehicleRequestDTO filter,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<GetVehicleResponseDTO> filteredVehicles = vehicleService.findVehicle(filter, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(filteredVehicles);
    }

    @GetMapping("/me")
    public ResponseEntity<List<GetVehicleResponseDTO>> findMyVehicles(
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        List<GetVehicleResponseDTO> myVehicles = vehicleService.findMyVehicles(jwtUserData);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(myVehicles);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UpdateVehicleResponseDTO> updateVehicle(
            @Valid @RequestBody
            UpdateVehicleRequestDTO request,
            @PathVariable
            UUID uuid
    ) {
        UpdateVehicleResponseDTO response = vehicleService.updateVehicle(uuid, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}

