package com.weg.WEGpark.rh.internal.listener.get;

import com.weg.WEGpark.rh.GetRhUserIdEvent;
import com.weg.WEGpark.rh.GetRhUserNameEvent;
import com.weg.WEGpark.rh.internal.app.service.RhService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RhGetListener {

    private final RhService rhService;

    @EventListener
    public void getUserName(GetRhUserNameEvent event) {
        rhService.getUserName(event);
    }

    @EventListener
    public void getUserId(GetRhUserIdEvent event) {
        rhService.getUserId(event);
    }
}
