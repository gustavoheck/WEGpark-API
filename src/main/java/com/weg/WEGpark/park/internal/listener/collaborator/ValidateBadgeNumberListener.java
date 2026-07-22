package com.weg.WEGpark.park.internal.listener.collaborator;

import com.weg.WEGpark.auth.ValidateBadgeNumberEvent;
import com.weg.WEGpark.park.ValidatedCollaboratorEvent;
import com.weg.WEGpark.park.internal.app.user.CollaboratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateBadgeNumberListener {

    private final CollaboratorService collaboratorService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @EventListener
    public void validateCollaborator (ValidateBadgeNumberEvent event) {
        Long id = collaboratorService.verifyCollaboratorToRegisterByBadgeNumber(event.request().badgeNumber());
        applicationEventPublisher.publishEvent(new ValidatedCollaboratorEvent(event.request(), id));
    }
}
