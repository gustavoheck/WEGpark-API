package com.weg.WEGpark.auth.internal.app.mapper;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;
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
}
