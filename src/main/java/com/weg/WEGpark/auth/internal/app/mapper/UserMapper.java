package com.weg.WEGpark.auth.internal.app.mapper;

import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.rh.RegisterGuardEvent;
import com.weg.WEGpark.rh.RegisterRhEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity (RegisterAccountRequestDTO request);

    User toEntityFromGuardEvent (RegisterGuardEvent event);

    User toEntityFromRhEvent (RegisterRhEvent event);

    RegisterAccountResponseDTO toResponse (User user);
}
