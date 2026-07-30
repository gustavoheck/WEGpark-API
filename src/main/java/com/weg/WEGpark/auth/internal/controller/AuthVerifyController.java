package com.weg.WEGpark.auth.internal.controller;

import com.weg.WEGpark.auth.internal.app.service.AuthNotificationService;
import com.weg.WEGpark.auth.internal.dto.defaults.EmailRequestDTO;
import com.weg.WEGpark.auth.internal.dto.reset.NewTokenResponseDTO;
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

    @GetMapping("/validate-email/{token}")
    public ResponseEntity<Void> activeAccountEmail (@PathVariable UUID token) {
        authNotificationService.validateAccount(token);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-email")
    public ResponseEntity<NewTokenResponseDTO> checkAccountEmail (
            EmailRequestDTO request
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
}
