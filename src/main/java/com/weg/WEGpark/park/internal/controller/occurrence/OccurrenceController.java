package com.weg.WEGpark.park.internal.controller.occurrence;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.internal.app.occurrence.service.OccurrenceService;
import com.weg.WEGpark.park.internal.app.occurrence.service.WarningService;
import com.weg.WEGpark.park.internal.dto.occurrence.filter.FilterOccurrenceRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/occurrence")
public class OccurrenceController {

    private final OccurrenceService occurrenceService;
    private final WarningService warningService;

    @GetMapping
    public ResponseEntity<Page<Record>> findOccurrences(
            FilterOccurrenceRequestDTO filter,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Page<Record> response = occurrenceService.findAllOccurrences(filter, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<Record>> findMyOccurrences(
            @AuthenticationPrincipal JWTUserData jwtUserData,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Record> response = occurrenceService.findMyOccurrences(jwtUserData, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
