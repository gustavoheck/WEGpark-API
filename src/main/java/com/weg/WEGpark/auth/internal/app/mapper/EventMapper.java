package com.weg.WEGpark.auth.internal.app.mapper;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.concurrent.CompletableFuture;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "defaults", target = ".")
    @Mapping(source = "parkUserDefaults", target = ".")
    CollaboratorRegisteredEvent toCollaboratorRegisteredEvent (
            RegisterCollaboratorRequestDTO request,
            CompletableFuture<RegisterAccountResponseDTO> futureResponse
    );

    @Mapping(source = "defaults", target = ".")
    @Mapping(source = "parkUserDefaults", target = ".")
    VisitorRegisteredEvent toVisitorRegisteredEvent (
            RegisterVisitorRequestDTO request,
            CompletableFuture<RegisterAccountResponseDTO> futureResponse
    );
}
