package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.auth.GuardRegisteredEvent;
import com.weg.WEGpark.park.GuardParkRegisteredEvent;
import com.weg.WEGpark.park.GuardUpdatedEvent;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.dto.user.guard.GetGuardResponseDTO;
import com.weg.WEGpark.rh.UpdateGuardEvent;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GuardMapper {
    Guard toEntity (GuardRegisteredEvent event);

    GuardParkRegisteredEvent toEventResponse (GuardRegisteredEvent event);

    @Mapping(source = "guard", target = ".")
    @Mapping(source = "active", target = "defaults.active")
    GetGuardResponseDTO toGetResponse (Guard guard, Boolean active);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromEvent(UpdateGuardEvent event, @MappingTarget Guard guard);

    GuardUpdatedEvent toUpdatedEvent(Guard guard);
}
