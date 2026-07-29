package com.weg.WEGpark.auth;

import com.weg.WEGpark.rh.RegisterGuardEvent;

public record ValidateCollaboratorByEvent (

        RegisterGuardEvent event
) {
}
