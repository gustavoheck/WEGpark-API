package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VisitorMapper {

    Visitor toEntity (VisitorRegisteredEvent event);
}
