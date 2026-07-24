package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociateWithVehicleResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParkUserMapper {

    AssociateWithVehicleResponseDTO toAssociationResponse (ParkUser parkUser);
}
