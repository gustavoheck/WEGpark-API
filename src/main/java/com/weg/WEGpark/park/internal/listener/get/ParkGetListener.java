package com.weg.WEGpark.park.internal.listener.get;

import com.weg.WEGpark.park.internal.app.user.service.ParkUserService;
import com.weg.WEGpark.park.GetParkUserIdEvent;
import com.weg.WEGpark.park.GetParkUserNameEvent;
import com.weg.WEGpark.rh.GetParkUsersEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParkGetListener {

    private final ParkUserService parkUserService;

    @EventListener
    public void getUsers (GetParkUsersEvent event) {
        parkUserService.findParkUsers(event, event.pageable());
    }

    @EventListener
    public void getUserName(GetParkUserNameEvent event) {
        parkUserService.getUserName(event);
    }

    @EventListener
    public void getUserId(GetParkUserIdEvent event) {
        parkUserService.getUserId(event);
    }
}
