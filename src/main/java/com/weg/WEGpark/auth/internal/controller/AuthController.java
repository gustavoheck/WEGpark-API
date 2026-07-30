package com.weg.WEGpark.auth.internal.controller;

import com.weg.WEGpark.auth.internal.app.service.AuthUpdateService;
import com.weg.WEGpark.auth.internal.app.service.LoginService;
import com.weg.WEGpark.auth.internal.app.service.RegisterService;
import com.weg.WEGpark.auth.internal.dto.defaults.EmailRequestDTO;
import com.weg.WEGpark.auth.internal.dto.login.LoginRequestDTO;
import com.weg.WEGpark.auth.internal.dto.login.LoginResponseDTO;
import com.weg.WEGpark.auth.internal.dto.login.SelectAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final LoginService loginService;
    private final RegisterService registerService;
    private final AuthUpdateService authUpdateService;

    @GetMapping
    public ResponseEntity<List<SelectAccountResponseDTO>> getUserRoles (
            @Valid @RequestBody EmailRequestDTO request
            ) {
        List<SelectAccountResponseDTO> response = loginService.preLogin(request.email());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login (@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = loginService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<UpdateUserResponseDTO> login (
            @Valid @RequestBody UpdateUserRequestDTO request) {
        UpdateUserResponseDTO response = authUpdateService.updateUserAuthDataRequest(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/register/collaborator")
    public CompletableFuture<ResponseEntity<RegisterAccountResponseDTO>> registerCollaborator (
            @Valid @RequestBody RegisterCollaboratorRequestDTO request
    ) {
        CompletableFuture<RegisterAccountResponseDTO> completableResponse =
                registerService.checkBadgeNumberBeforeRegistering(request);

        return completableResponse.thenApply(response -> {
            URI uri = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{uuid}")
                    .buildAndExpand(response.uuid())
                    .toUri();

            return ResponseEntity.created(uri).body(response);
        });
    }

    @PostMapping("/register/visitor")
    public CompletableFuture<ResponseEntity<RegisterAccountResponseDTO>> registerVisitor (
            @Valid @RequestBody RegisterVisitorRequestDTO request
    ) {
        CompletableFuture<RegisterAccountResponseDTO> completableResponse =
                registerService.checkVisitorAccountsBeforeRegistering(request);

        return completableResponse.thenApply(response -> {
            URI uri = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{uuid}")
                    .buildAndExpand(response.uuid())
                    .toUri();

            return ResponseEntity.created(uri).body(response);
        });
    }

    @PostMapping("/admin")
    public ResponseEntity<RegisterAccountResponseDTO> registerAdmin () {
        RegisterAccountResponseDTO response = registerService.registerAdminAccount();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
