package com.weg.WEGpark.rh.internal.app.mapper;

import com.weg.WEGpark.auth.DefaultRegisteredEvent;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.rh.RegisterRhEvent;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.dto.rh.RegisterRhRequestDTO;
import com.weg.WEGpark.rh.internal.dto.rh.RegisterRhResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.concurrent.CompletableFuture;

@Mapper(componentModel = "spring")
public interface RhMapper {

    Rh toEntity (RegisterRhRequestDTO request);

    @Mapping(source = "request", target = ".")
    RegisterRhEvent toRegisterEvent (RegisterAccountRequestDTO request, CompletableFuture<DefaultRegisteredEvent> eventResponse);

    RegisterRhResponseDTO toRegisterResponse (Rh rh);
}
