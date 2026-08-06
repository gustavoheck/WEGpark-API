package com.weg.WEGpark.rh.internal.app.mapper;

import com.weg.WEGpark.auth.DefaultRegisteredEvent;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountRequestDTO;
import com.weg.WEGpark.rh.RegisterRhEvent;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.dto.rh.*;
import org.mapstruct.*;

import java.util.concurrent.CompletableFuture;

@Mapper(componentModel = "spring")
public interface RhMapper {

    Rh toEntity (RegisterRhRequestDTO request);

    @Mapping(source = "request", target = ".")
    RegisterRhEvent toRegisterEvent (RegisterAccountRequestDTO request, CompletableFuture<DefaultRegisteredEvent> eventResponse);

    @Mapping(source = "rh", target = "defaults")
    RegisterRhResponseDTO toRegisterResponse (Rh rh);

    UpdateRhResponseDTO toUpdateResponse (Rh rh);

    GetRhResponseDTO toGetResponse (Rh rh);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(UpdateRhRequestDTO request, @MappingTarget Rh rh);
}
