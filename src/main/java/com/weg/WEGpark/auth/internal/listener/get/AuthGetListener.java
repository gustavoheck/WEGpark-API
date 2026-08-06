package com.weg.WEGpark.auth.internal.listener.get;

import com.weg.WEGpark.auth.GetAuthUserIdEvent;
import com.weg.WEGpark.auth.GetUsersActiveEvent;
import com.weg.WEGpark.auth.internal.app.service.AuthUserService;
import com.weg.WEGpark.rh.GetRhUserActiveEvent;
import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthGetListener {

    private final AuthUserService authUserService;

    @EventListener
    public void getActive (IsParkUserActiveEvent isParkUserActiveEvent) {
        authUserService.getActive(isParkUserActiveEvent.targetUserUuid(), isParkUserActiveEvent.eventResponse());
    }

    @EventListener
    public void getUsersActive (GetUsersActiveEvent event) {
        authUserService.getUsersActive(event);
    }

    @EventListener
    public void getUserId(GetAuthUserIdEvent event) {
        authUserService.getUserId(event);
    }

    @EventListener
    public void getUserId(GetRhUserActiveEvent event) {
        authUserService.getActive(event.userUuid(), event.eventResponse());
    }
}
