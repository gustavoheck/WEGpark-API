package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.auth.GuardRegisteredEvent;
import com.weg.WEGpark.park.ParkGuardRegisteredEvent;
import com.weg.WEGpark.park.GuardUpdatedEvent;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.dto.user.defaults.GetParkUserResponseDTO;
import com.weg.WEGpark.park.internal.dto.user.guard.GetGuardResponseDTO;
import com.weg.WEGpark.rh.UpdateGuardEvent;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GuardMapper {
    Guard toEntity (GuardRegisteredEvent event);

    ParkGuardRegisteredEvent toEventResponse (GuardRegisteredEvent event);

    @Mapping(source = "guard", target = ".")
    @Mapping(target = "defaults", expression = "java(toGetResponseDefaults(guard, active))")
    GetGuardResponseDTO toGetResponse (Guard guard, Boolean active);

    default GetParkUserResponseDTO toGetResponseDefaults(Guard guard, Boolean active) {
        if (guard == null || active == null) {
            return null;
        }
        return new GetParkUserResponseDTO(
                guard.getUuid(),
                guard.getEmail(),
                guard.getTelephone(),
                guard.getName(),
                active,
                guard.getUserType()
        );
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromEvent(UpdateGuardEvent event, @MappingTarget Guard guard);

    GuardUpdatedEvent toUpdatedEvent(Guard guard);
}
