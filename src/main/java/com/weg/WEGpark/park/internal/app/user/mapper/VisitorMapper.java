package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.internal.dto.user.defaults.GetParkUserResponseDTO;
import com.weg.WEGpark.park.internal.dto.user.visitor.GetVisitorResponseDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateVisitorRequestDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateVisitorResponseDTO;
import com.weg.WEGpark.rh.UpdateVisitorEvent;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VisitorMapper {

    Visitor toEntity (VisitorRegisteredEvent event);

    @Mapping(source = "visitor", target = ".")
    @Mapping(target = "defaults", expression = "java(toGetResponseDefaults(visitor, active))")
    GetVisitorResponseDTO toResponse (Visitor visitor, Boolean active);

    default GetParkUserResponseDTO toGetResponseDefaults(Visitor visitor, Boolean active) {
        if (visitor == null || active == null) {
            return null;
        }
        return new GetParkUserResponseDTO(
                visitor.getUuid(),
                visitor.getEmail(),
                visitor.getTelephone(),
                visitor.getName(),
                active,
                visitor.getUserType()
        );
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "request", target = ".")
    void updateFromDTO(UpdateVisitorRequestDTO request, @MappingTarget Visitor visitor);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromEvent(UpdateVisitorEvent event, @MappingTarget Visitor visitor);

    @Mapping(source = ".", target = "defaults")
    UpdateVisitorResponseDTO toUpdateResponse (Visitor visitor);

    @Mapping(source = ".", target = "defaults")
    UpdateVisitorResponseDTO toUpdateResponseFromEvent (UpdateVisitorEvent event);
}
