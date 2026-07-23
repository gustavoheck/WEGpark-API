package com.weg.WEGpark.park.internal.listener.validation;

import com.weg.WEGpark.auth.ValidateCollaboratorEvent;
import com.weg.WEGpark.park.ValidatedCollaboratorEvent;
import com.weg.WEGpark.park.internal.app.user.service.CollaboratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateCollaboratorListener {

    private final CollaboratorService collaboratorService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @EventListener
    public void validateCollaborator (ValidateCollaboratorEvent event) {
        Long id = collaboratorService.verifyCollaboratorToRegister(event.request().badgeNumber(), event.request().defaults().email());
        applicationEventPublisher.publishEvent(new ValidatedCollaboratorEvent(event.futureResponse(), event.request(), id));
    }
}
