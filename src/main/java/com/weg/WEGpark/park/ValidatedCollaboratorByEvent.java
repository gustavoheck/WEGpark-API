package com.weg.WEGpark.park;

import com.weg.WEGpark.rh.RegisterGuardEvent;

public record ValidatedCollaboratorByEvent(

        RegisterGuardEvent event,

        Long collaboratorId
) {
}
