package com.weg.WEGpark.auth.internal.listener.register;

import com.weg.WEGpark.auth.internal.app.service.RegisterService;
import com.weg.WEGpark.park.ValidatedCollaboratorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollaboratorRegisteringListener {

    private final RegisterService registerService;

    @EventListener
    public void startRegistering (ValidatedCollaboratorEvent event) {
        registerService.registerCollaborator(event.request(), event.collaboratorId());
    }
}
