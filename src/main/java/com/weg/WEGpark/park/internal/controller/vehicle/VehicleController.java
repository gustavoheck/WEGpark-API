package com.weg.WEGpark.park.internal.controller.vehicle;

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
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping
    public ResponseEntity<CreateVehicleResponseDTO> registerVehicle(
            @Valid @RequestBody CreateVehicleRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CreateVehicleResponseDTO response = vehicleService.registerVehicle(request, userDetails);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(response.uuid())
                .toUri();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PostMapping("/associate/notification/{id}")
    public ResponseEntity<AssociateWithVehicleResponseDTO> associateVehicle (@PathVariable Long id) {
        AssociateWithVehicleResponseDTO response = vehicleService.associateToRegisteredVehicle(id);

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
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        vehicleService.SendNotificationForAssociate(request, userDetails);

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

