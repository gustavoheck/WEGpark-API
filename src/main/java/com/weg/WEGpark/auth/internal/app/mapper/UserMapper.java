package com.weg.WEGpark.auth.internal.app.mapper;

import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountRequestDTO;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity (RegisterAccountRequestDTO request);

    RegisterAccountResponseDTO toResponse (User user);
}
