package com.weg.WEGpark.auth.internal.controller;

import com.weg.WEGpark.auth.internal.app.service.AuthNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthVerifyController {

    private final AuthNotificationService authNotificationService;

    @PostMapping
    public ResponseEntity<Void> activeAccountEmail (@PathVariable UUID token) {
        authNotificationService.validateAccount(token);

        return ResponseEntity.noContent().build();
    }
}
