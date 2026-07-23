package com.weg.WEGpark.park.internal.listener.register;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.park.internal.app.user.service.CollaboratorService;
import com.weg.WEGpark.park.internal.app.user.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterCollaboratorVisitorListener {

    private final CollaboratorService collaboratorService;
    private final VisitorService visitorService;

    @EventListener
    public void registerCollaborator (CollaboratorRegisteredEvent event) {
        collaboratorService.registerCollaborator(event);
    }

    @EventListener
    public void registerVisitor (VisitorRegisteredEvent event) {
        visitorService.registerVisitor(event);
    }
}
