package com.weg.WEGpark.rh.internal.app.mapper;

import com.weg.WEGpark.auth.UpdateUserAuthEvent;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mapper(componentModel = "spring")
public interface UserOperationMapper {

    @Mapping(source = "request", target = ".")
    UpdateUserAuthEvent toUpdateAuthEvent (
            UpdateUserRequestDTO request,
            CompletableFuture<UpdateUserResponseDTO> eventResponse,
            UUID targetUuid
    );
}
