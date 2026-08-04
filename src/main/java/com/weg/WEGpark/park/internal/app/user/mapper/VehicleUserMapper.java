package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.GetVehicleUserResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleUserMapper {

    default GetVehicleUserResponseDTO toResponse (VehicleUser vehicleUser) {
        return switch (vehicleUser.getParkUser()) {
            case Guard guard -> toResponse(vehicleUser, guard.getBadgeNumber(), guard.getLocation(), guard.getBoss(), null);
            case Collaborator collaborator -> toResponse(
                    vehicleUser, collaborator.getBadgeNumber(), collaborator.getLocation(), null, null);
            case Visitor visitor -> toResponse(vehicleUser, null, null, null, visitor.getCompany());
            default -> toResponse(vehicleUser, null, null, null, null);
        };
    }

    private GetVehicleUserResponseDTO toResponse (
            VehicleUser vehicleUser,
            String badgeNumber,
            String location,
            String boss,
            String company
    ) {
        ParkUser parkUser = vehicleUser.getParkUser();

        return new GetVehicleUserResponseDTO(
                parkUser.getUuid(),
                vehicleUser.getVehicleOwner(),
                parkUser.getTelephone(),
                vehicleUser.getActive(),
                parkUser.getName(),
                parkUser.getUserType(),
                badgeNumber,
                location,
                boss,
                company
        );
    }
}
