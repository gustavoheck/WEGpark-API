package com.weg.WEGpark.rh.internal.controller;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.rh.internal.app.service.UserOperationService;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rh")
public class RhControllerOperation {

    private final UserOperationService userOperationService;

    @GetMapping
    public ResponseEntity<Page<Record>> listUsers (FindUserFilter filter, Pageable pageable) {
        Page<Record> page = userOperationService.listUsers(filter, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(page);
    }

    @GetMapping("/user/{userUuid}")
    public ResponseEntity<Record> findUser (
            @PathVariable UUID userUuid,
            @RequestParam RolesType role
    ) {
        return ResponseEntity.ok(userOperationService.findUser(userUuid, role));
    }

    @PutMapping("/user/{userUuid}")
    public ResponseEntity<UpdateUserResponseDTO> updateUser (
            @Valid @RequestBody UpdateUserRequestDTO request,
            @PathVariable UUID userUuid,
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        UpdateUserResponseDTO response = userOperationService.updateUserAuthData(request, userUuid, jwtUserData);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/user/{userUuid}/desactivate")
    public ResponseEntity<Void> desactivateUser (
            @PathVariable UUID userUuid,
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        userOperationService.desactivateAndActivateUser(userUuid, jwtUserData);

        return ResponseEntity.noContent().build();
    }
}
