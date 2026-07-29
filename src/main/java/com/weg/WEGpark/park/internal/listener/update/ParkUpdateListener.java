package com.weg.WEGpark.park.internal.listener.update;

import com.weg.WEGpark.park.internal.app.user.service.CollaboratorService;
import com.weg.WEGpark.park.internal.app.user.service.GuardService;
import com.weg.WEGpark.park.internal.app.user.service.VisitorService;
import com.weg.WEGpark.rh.UpdateCollaboratorEvent;
import com.weg.WEGpark.rh.UpdateGuardEvent;
import com.weg.WEGpark.rh.UpdateVisitorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParkUpdateListener {

    private final CollaboratorService collaboratorService;
    private final VisitorService visitorService;
    private final GuardService guardService;

    @EventListener
    public void updateCollaborator (UpdateCollaboratorEvent event) {
        collaboratorService.updateCollaboratorEvent(event);
    }

    @EventListener
    public void updateGuard (UpdateGuardEvent event) {
        guardService.updateGuard(event);
    }

    @EventListener
    public void updateVisitor (UpdateVisitorEvent event) {
        visitorService.updateVisitorEvent(event);
    }

}
