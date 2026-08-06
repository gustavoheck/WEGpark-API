package com.weg.WEGpark.park.internal.controller.user;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.park.internal.app.user.service.CollaboratorService;
import com.weg.WEGpark.park.internal.app.user.service.ParkUserService;
import com.weg.WEGpark.park.internal.app.user.service.VisitorService;
import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorRequestDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorResponseDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateVisitorRequestDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateVisitorResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/park")
public class ParkUserController {

    private final ParkUserService parkUserService;
    private final CollaboratorService collaboratorService;
    private final VisitorService visitorService;

    @GetMapping("/profile")
    public ResponseEntity<Record> findMyProfile(
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        return ResponseEntity.ok(parkUserService.findMyProfile(jwtUserData));
    }

    @PatchMapping("/profile/collaborator")
    public ResponseEntity<UpdateCollaboratorResponseDTO> updateCollaboratorProfile(
            @RequestBody @Valid UpdateCollaboratorRequestDTO request,
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        return ResponseEntity.ok(collaboratorService.updateCollaboratorRequest(request, jwtUserData));
    }

    @PatchMapping("/profile/visitor")
    public ResponseEntity<UpdateVisitorResponseDTO> updateVisitorProfile(
            @RequestBody @Valid UpdateVisitorRequestDTO request,
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        return ResponseEntity.ok(visitorService.updateVisitorRequest(request, jwtUserData));
    }
}
