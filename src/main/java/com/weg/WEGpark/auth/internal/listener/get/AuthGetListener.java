package com.weg.WEGpark.auth.internal.listener.get;

import com.weg.WEGpark.auth.internal.app.service.UserService;
import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthGetListener {

    private final UserService userService;

    @EventListener
    public void getActive (IsParkUserActiveEvent isParkUserActiveEvent) {
        userService.getActive(isParkUserActiveEvent);
    }
}
