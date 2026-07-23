package com.weg.WEGpark.auth.internal.controller;

import com.weg.WEGpark.auth.internal.app.service.LoginService;
import com.weg.WEGpark.auth.internal.app.service.RegisterService;
import com.weg.WEGpark.auth.internal.dto.login.LoginRequestDTO;
import com.weg.WEGpark.auth.internal.dto.login.LoginResponseDTO;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final LoginService loginService;
    private final RegisterService registerService;

    @RequestMapping("/login")
    public ResponseEntity<LoginResponseDTO> login (@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = loginService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @RequestMapping("/register/collaborator")
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

    @RequestMapping("/register/visitor")
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
}
