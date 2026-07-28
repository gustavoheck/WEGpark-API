package com.weg.WEGpark.park.internal.listener.update;

import com.weg.WEGpark.park.internal.app.user.service.CollaboratorService;
import com.weg.WEGpark.park.internal.app.user.service.GuardService;
import com.weg.WEGpark.rh.UpdateCollaboratorEvent;
import com.weg.WEGpark.rh.UpdateGuardEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParkUpdateListener {

    private final CollaboratorService collaboratorService;
    private final GuardService guardService;

    @EventListener
    public void UpdateCollaborator (UpdateCollaboratorEvent event) {
        collaboratorService.updateCollaboratorEvent(event);
    }

    @EventListener
    public void UpdateGuard (UpdateGuardEvent event) {
        guardService.updateGuard(event);
    }
}
