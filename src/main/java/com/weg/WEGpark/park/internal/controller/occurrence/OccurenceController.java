package com.weg.WEGpark.park.internal.controller.occurrence;

import com.weg.WEGpark.park.internal.app.occurrence.service.OccurrenceService;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceRequestDto;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<OccurrenceResponseDto>> getAllOccurrences() {

        List<OccurrenceResponseDto> response = occurrenceService.findAllOccurrences();

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/date")
    public ResponseEntity<List<OccurrenceResponseDto>> getOccurrencesByDate(
            @RequestParam(name = "period")
            @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth period
    ) {

        List<OccurrenceResponseDto> response = occurrenceService.findByDate(period);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

}
