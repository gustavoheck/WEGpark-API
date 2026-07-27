package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.auth.GuardRegisteredEvent;
import com.weg.WEGpark.park.GuardParkRegisteredEvent;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GuardMapper {
    Guard toEntity (GuardRegisteredEvent event);

    GuardParkRegisteredEvent toEventResponse (GuardRegisteredEvent event);
}
