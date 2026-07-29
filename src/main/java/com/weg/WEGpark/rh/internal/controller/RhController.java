package com.weg.WEGpark.rh.internal.controller;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.rh.internal.app.service.RhGuardService;
import com.weg.WEGpark.rh.internal.app.service.RhService;
import com.weg.WEGpark.rh.internal.app.service.UserOperationService;
import com.weg.WEGpark.rh.internal.dto.rh.RegisterRhRequestDTO;
import com.weg.WEGpark.rh.internal.dto.rh.RegisterRhResponseDTO;
import com.weg.WEGpark.rh.internal.dto.rh.UpdateRhRequestDTO;
import com.weg.WEGpark.rh.internal.dto.rh.UpdateRhResponseDTO;
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
@RequestMapping("/rh")
public class RhController {

    private final RhService rhService;
    private final RhGuardService rhGuardService;
    private final UserOperationService userOperationService;

    @PostMapping
    public ResponseEntity<RegisterRhResponseDTO> registerRh (
            @RequestBody @Valid RegisterRhRequestDTO request,
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        RegisterRhResponseDTO response = rhService.registerRh(request, jwtUserData);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(response.defaults().uuid())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(response);
    }

    @PutMapping("/{rhUuid}")
    public ResponseEntity<UpdateRhResponseDTO> updateRh (
            @RequestBody @Valid UpdateRhRequestDTO request,
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PathVariable UUID rhUuid
    ) {
        UpdateRhResponseDTO response = rhService.updateRh(request, rhUuid, jwtUserData);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
