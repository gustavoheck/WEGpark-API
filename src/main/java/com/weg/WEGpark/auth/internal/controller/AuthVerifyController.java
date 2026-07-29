package com.weg.WEGpark.auth.internal.controller;

import com.weg.WEGpark.auth.internal.app.service.AuthNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
