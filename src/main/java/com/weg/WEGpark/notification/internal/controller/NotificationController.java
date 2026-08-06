package com.weg.WEGpark.notification.internal.controller;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.notification.internal.app.notification.service.NotificationService;
import com.weg.WEGpark.notification.internal.dto.GetNotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<GetNotificationResponseDTO>> findMyNotifications(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PageableDefault(size = 5, sort = "notificationTime", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(notificationService.findMyNotifications(jwtUserData, pageable));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal JWTUserData jwtUserData
    ) {
        notificationService.deleteNotification(uuid, jwtUserData);

        return ResponseEntity.noContent().build();
    }
}
