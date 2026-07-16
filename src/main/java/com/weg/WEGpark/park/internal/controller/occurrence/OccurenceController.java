package com.weg.WEGpark.park.internal.controller.occurrence;

import com.weg.WEGpark.park.internal.app.occurrence.service.OccurrenceService;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceRequestDto;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/occurrence")
public class OccurenceController {

    private final OccurrenceService occurrenceService;

    @PostMapping
    public ResponseEntity<OccurrenceResponseDto> postOccurrence(@Valid @RequestBody OccurrenceRequestDto request) {
        OccurrenceResponseDto response = occurrenceService.registerOccurrence(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
