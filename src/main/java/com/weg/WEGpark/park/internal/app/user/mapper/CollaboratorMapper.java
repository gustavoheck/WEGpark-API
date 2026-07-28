package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.dto.user.collaborator.GetCollaboratorResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {

    Collaborator toEntity (CollaboratorRegisteredEvent event);

    GetCollaboratorResponseDTO toResponse (Collaborator collaborator);
}
