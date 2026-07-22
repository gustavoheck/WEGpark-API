package com.weg.WEGpark.auth.internal.app.mapper;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.auth.shared.dto.collaborator.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.auth.shared.dto.visitor.RegisterVisitorRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "defaults", target = ".")
    @Mapping(source = "parkUserDefaults", target = ".")
    CollaboratorRegisteredEvent toCollaboratorRegisteredEvent (RegisterCollaboratorRequestDTO request);

    @Mapping(source = "defaults", target = ".")
    @Mapping(source = "parkUserDefaults", target = ".")
    VisitorRegisteredEvent toVisitorRegisteredEvent (RegisterVisitorRequestDTO request);
}
