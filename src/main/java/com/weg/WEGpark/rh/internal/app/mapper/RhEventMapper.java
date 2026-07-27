package com.weg.WEGpark.rh.internal.app.mapper;

import com.weg.WEGpark.auth.GuardRegisteredEvent;
import com.weg.WEGpark.park.GuardParkRegisteredEvent;
import com.weg.WEGpark.rh.RegisterGuardEvent;
import com.weg.WEGpark.rh.internal.dto.guard.RegisterGuardRequestDTO;
import com.weg.WEGpark.rh.internal.dto.guard.RegisterGuardResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.concurrent.CompletableFuture;

@Mapper(componentModel = "spring")
public interface RhEventMapper {

    @Mapping(source = "request.collaboratorDefaults", target = ".")
    @Mapping(source = "request.collaboratorDefaults.defaults", target = ".")
    @Mapping(source = "request.collaboratorDefaults.parkUserDefaults", target = ".")
    RegisterGuardEvent toGuardRegisterEvent (RegisterGuardRequestDTO request, CompletableFuture<GuardParkRegisteredEvent> registerResponse);

    RegisterGuardResponseDTO toGuardRegisterResponse (GuardParkRegisteredEvent event);
}
