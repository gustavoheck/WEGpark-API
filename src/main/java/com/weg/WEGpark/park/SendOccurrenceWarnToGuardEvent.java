package com.weg.WEGpark.park;

import java.util.List;

public record SendOccurrenceWarnToGuardEvent(
        List<Long> allGuardsId,

        List<String> allGuardsEmail,

        String message
) {
}
