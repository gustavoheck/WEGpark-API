package com.weg.WEGpark.rh.internal.app.mapper;

import com.weg.WEGpark.park.ParkGuardRegisteredEvent;
import com.weg.WEGpark.park.GuardUpdatedEvent;
import com.weg.WEGpark.rh.RegisterGuardEvent;
import com.weg.WEGpark.rh.UpdateGuardEvent;
import com.weg.WEGpark.rh.internal.dto.guard.RegisterGuardRequestDTO;
import com.weg.WEGpark.rh.internal.dto.guard.RegisterGuardResponseDTO;
import com.weg.WEGpark.rh.internal.dto.guard.UpdateGuardRequestDTO;
import com.weg.WEGpark.rh.internal.dto.guard.UpdateGuardResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mapper(componentModel = "spring")
public interface RhGuardMapper {

    @Mapping(source = "request.collaboratorDefaults", target = ".")
    @Mapping(source = "request.collaboratorDefaults.defaults", target = ".")
    @Mapping(source = "request.collaboratorDefaults.parkUserDefaults", target = ".")
    RegisterGuardEvent toGuardRegisterEvent (RegisterGuardRequestDTO request, CompletableFuture<ParkGuardRegisteredEvent> registerResponse);

    RegisterGuardResponseDTO toGuardRegisterResponse (ParkGuardRegisteredEvent event);

    UpdateGuardEvent toGuardUpdateEvent (UpdateGuardRequestDTO request, UUID uuid, CompletableFuture<GuardUpdatedEvent> eventResponse);

    UpdateGuardResponseDTO toGuardUpdateResponse (GuardUpdatedEvent guardUpdatedEvent);
}
