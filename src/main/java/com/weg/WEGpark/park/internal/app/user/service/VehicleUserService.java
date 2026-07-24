package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.infra.repository.VehicleUserRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleUserService {

    private final VehicleUserRepository vehicleUserRepository;

    public void disassociateVehicle (UUID vehicleUuid, JWTUserData jwtUserData) {
        VehicleUser vehicleUser = vehicleUserRepository.findByVehicleUuidAndParkUserUuid(vehicleUuid, jwtUserData.uuid())
                .orElseThrow(() -> new NotFoundException("Any vehicle user was found by this uuid"));

        if (vehicleUser.getVehicleOwner()) {
            vehicleUser.getVehicle().getParkUsers()
                    .forEach(user -> user.setActive(false));
        } else {
            vehicleUser.setActive(false);
        }
    }
}
