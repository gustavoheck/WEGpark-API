package com.weg.WEGpark.rh.internal.controller;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.rh.internal.app.service.GuardService;
import com.weg.WEGpark.rh.internal.dto.guard.RegisterGuardRequestDTO;
import com.weg.WEGpark.rh.internal.dto.guard.RegisterGuardResponseDTO;
import com.weg.WEGpark.rh.internal.dto.guard.UpdateGuardRequestDTO;
import com.weg.WEGpark.rh.internal.dto.guard.UpdateGuardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rh/guard")
public class RhControllerGuard {

    private final GuardService guardService;

    @PutMapping("/{uuid}")
    public ResponseEntity<UpdateGuardResponseDTO> updateGuard (
            @RequestBody @Valid UpdateGuardRequestDTO request,
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PathVariable UUID guardUuid
    ) {
        UpdateGuardResponseDTO response = guardService.updateRegistrationData(request, guardUuid, jwtUserData);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping
    public ResponseEntity<RegisterGuardResponseDTO> registerGuard (
            @RequestBody @Valid RegisterGuardRequestDTO request,
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        RegisterGuardResponseDTO response = guardService.createGuard(request, jwtUserData);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(response.uuid())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(response);
    }
}
