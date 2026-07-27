package com.weg.WEGpark.auth.internal.listener.update;

import com.weg.WEGpark.auth.UpdateUserAuthEvent;
import com.weg.WEGpark.auth.internal.app.service.UpdateService;
import com.weg.WEGpark.rh.DesactivateAndActivateUserEvent;
import com.weg.WEGpark.rh.RegisterRhEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateListener {

    private final UpdateService updateService;

    @EventListener
    public void updateUser (UpdateUserAuthEvent event) {
        updateService.updateUserAuthDataEvent(event);
    }

    @EventListener
    public void activateDesactivateUser (DesactivateAndActivateUserEvent event) {
        updateService.activateAndDesactivateUser(event);
    }
}
