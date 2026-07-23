package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociateWithVehicleResponseDTO;

public interface ParkUserMapper {

    AssociateWithVehicleResponseDTO toAssociationResponse (ParkUser parkUser);
}
