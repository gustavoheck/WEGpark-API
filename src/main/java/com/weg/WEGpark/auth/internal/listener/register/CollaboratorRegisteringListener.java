package com.weg.WEGpark.auth.internal.listener.register;

import com.weg.WEGpark.auth.internal.app.service.RegisterService;
import com.weg.WEGpark.park.ValidatedCollaboratorByEvent;
import com.weg.WEGpark.park.ValidatedCollaboratorEvent;
import com.weg.WEGpark.rh.RegisterGuardEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollaboratorRegisteringListener {

    private final RegisterService registerService;

    @EventListener
    public void startRegistering (ValidatedCollaboratorEvent event) {
        registerService.registerCollaborator(event.futureResponse(), event.request(), event.collaboratorId());
    }
    
    @EventListener
    public void startRegisteringGuard (RegisterGuardEvent event) {
        registerService.checkBadgeNumberBeforeRegisteringEvent(event);
    }

    public void registeringGuard (ValidatedCollaboratorByEvent event) {
        registerService.registerGuard(event.event(), event.collaboratorId());
    }
}
