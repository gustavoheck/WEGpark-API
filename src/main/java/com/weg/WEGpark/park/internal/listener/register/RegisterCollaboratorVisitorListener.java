package com.weg.WEGpark.park.internal.listener.register;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterCollaboratorVisitorListener {

    @EventListener
    public void registerCollaborator (CollaboratorRegisteredEvent event) {

    }
}
