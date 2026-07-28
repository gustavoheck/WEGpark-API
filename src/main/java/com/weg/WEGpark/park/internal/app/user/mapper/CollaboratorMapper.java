package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.dto.user.collaborator.GetCollaboratorResponseDTO;
import com.weg.WEGpark.park.internal.dto.user.collaborator.UpdateCollaboratorRequestDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorResponseDTO;
import com.weg.WEGpark.rh.UpdateCollaboratorEvent;
import com.weg.WEGpark.rh.UpdateGuardEvent;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {

    Collaborator toEntity (CollaboratorRegisteredEvent event);

    GetCollaboratorResponseDTO toResponse (Collaborator collaborator);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "event", target = ".")
    void updateFromDTO(UpdateCollaboratorRequestDTO request, @MappingTarget Collaborator collaborator);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromEvent(UpdateCollaboratorEvent event, @MappingTarget Collaborator collaborator);

    @Mapping(source = ".", target = "defaults")
    UpdateCollaboratorResponseDTO toUpdateResponse (Collaborator collaborator);

    @Mapping(source = ".", target = "defaults")
    UpdateCollaboratorResponseDTO toUpdateResponseFromEvent (UpdateCollaboratorEvent event);
}
