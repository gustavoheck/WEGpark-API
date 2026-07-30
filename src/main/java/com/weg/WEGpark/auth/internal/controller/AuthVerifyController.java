package com.weg.WEGpark.auth.internal.controller;

import com.weg.WEGpark.auth.internal.app.service.AuthNotificationService;
import com.weg.WEGpark.auth.internal.app.service.AuthTokenService;
import com.weg.WEGpark.auth.internal.dto.defaults.EmailRequestDTO;
import com.weg.WEGpark.auth.internal.dto.reset.NewTokenResponseDTO;
import com.weg.WEGpark.auth.internal.dto.reset.NumberTokenVerificateTryRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthVerifyController {

    private final AuthNotificationService authNotificationService;
    private final AuthTokenService authTokenService;

    @GetMapping("/validate-email/{token}")
    public ResponseEntity<Void> activeAccountEmail (@PathVariable UUID token) {
        authNotificationService.validateAccount(token);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password/check-email")
    public ResponseEntity<NewTokenResponseDTO> checkAccountEmail (
            @Valid @RequestBody EmailRequestDTO request
    ) {
        NewTokenResponseDTO response = authNotificationService.resetPasswordEmailCheck(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{token}")
                .buildAndExpand(response.token())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(response);
    }

    @PostMapping("/reset-password/check-email/answer")
    public ResponseEntity<NewTokenResponseDTO> checkAccountEmailAnswer (
            @RequestBody @Valid NumberTokenVerificateTryRequestDTO request
    ) {
        NewTokenResponseDTO response = authTokenService.validateNumberToken(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{token}")
                .buildAndExpand(response.token())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(response);
    }
}
