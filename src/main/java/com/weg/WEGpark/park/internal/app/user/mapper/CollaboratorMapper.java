package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.dto.user.collaborator.GetCollaboratorResponseDTO;
import com.weg.WEGpark.park.internal.dto.user.defaults.GetParkUserResponseDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorRequestDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorResponseDTO;
import com.weg.WEGpark.rh.UpdateCollaboratorEvent;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {

    Collaborator toEntity (CollaboratorRegisteredEvent event);

    @Mapping(source = "collaborator", target = ".")
    @Mapping(target = "defaults", expression = "java(toGetResponseDefaults(collaborator, active))")
    GetCollaboratorResponseDTO toResponse (Collaborator collaborator, Boolean active);

    default GetParkUserResponseDTO toGetResponseDefaults(Collaborator collaborator, Boolean active) {
        if (collaborator == null || active == null) {
            return null;
        }
        return new GetParkUserResponseDTO(
                collaborator.getUuid(),
                collaborator.getEmail(),
                collaborator.getTelephone(),
                collaborator.getName(),
                active,
                collaborator.getUserType()
        );
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "request", target = ".")
    void updateFromDTO(UpdateCollaboratorRequestDTO request, @MappingTarget Collaborator collaborator);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromEvent(UpdateCollaboratorEvent event, @MappingTarget Collaborator collaborator);

    @Mapping(source = ".", target = "defaults")
    UpdateCollaboratorResponseDTO toUpdateResponse (Collaborator collaborator);

    @Mapping(source = ".", target = "defaults")
    UpdateCollaboratorResponseDTO toUpdateResponseFromEvent (UpdateCollaboratorEvent event);
}
