package com.weg.WEGpark.auth.internal.app.mapper;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.GuardRegisteredEvent;
import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;
import com.weg.WEGpark.rh.RegisterGuardEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.concurrent.CompletableFuture;

@Mapper(componentModel = "spring")
public interface AuthEventMapper {

    @Mapping(source = "request.defaults", target = ".")
    @Mapping(source = "request.parkUserDefaults", target = ".")
    @Mapping(source = "user.uuid", target = "uuid")
    CollaboratorRegisteredEvent toCollaboratorRegisteredEvent (
            RegisterCollaboratorRequestDTO request,
            CompletableFuture<RegisterAccountResponseDTO> futureResponse,
            User user
    );

    @Mapping(source = "request.defaults", target = ".")
    @Mapping(source = "request.parkUserDefaults", target = ".")
    @Mapping(source = "user.uuid", target = "uuid")
    VisitorRegisteredEvent toVisitorRegisteredEvent (
            RegisterVisitorRequestDTO request,
            CompletableFuture<RegisterAccountResponseDTO> futureResponse,
            User user
    );

    @Mapping(source = "event", target = ".")
    @Mapping(source = "user.uuid", target = "uuid")
    @Mapping(source = "user.id", target = "id")
    GuardRegisteredEvent ToGuardRegisteredEvent (
            RegisterGuardEvent event,
            User user
    );
}
