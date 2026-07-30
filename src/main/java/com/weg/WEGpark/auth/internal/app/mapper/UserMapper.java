package com.weg.WEGpark.auth.internal.app.mapper;

import com.weg.WEGpark.auth.UpdateUserAuthEvent;
import com.weg.WEGpark.auth.internal.app.dto.UpdateUserDTO;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.rh.RegisterGuardEvent;
import com.weg.WEGpark.rh.RegisterRhEvent;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity (RegisterAccountRequestDTO request);

    User toEntityFromGuardEvent (RegisterGuardEvent event);

    User toEntityFromRhEvent (RegisterRhEvent event);

    RegisterAccountResponseDTO toRegisterResponse (User user);

    UpdateUserResponseDTO toUpdateResponse (User user);

    UpdateUserDTO toUpdateDto (UpdateUserRequestDTO request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(UpdateUserDTO dto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromEvent(UpdateUserAuthEvent event, @MappingTarget User user);

}
