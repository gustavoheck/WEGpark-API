package com.weg.WEGpark.auth.internal.app.mapper;

import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountRequestDTO;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.rh.RegisterGuardEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity (RegisterAccountRequestDTO request);

    User toEntityFromGuardEvent (RegisterGuardEvent event);

    RegisterAccountResponseDTO toResponse (User user);
}
