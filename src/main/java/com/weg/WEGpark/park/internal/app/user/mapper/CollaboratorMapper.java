package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {

    Collaborator toEntity (CollaboratorRegisteredEvent event);

}
