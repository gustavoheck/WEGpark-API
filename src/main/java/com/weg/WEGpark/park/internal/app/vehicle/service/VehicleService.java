package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyRegisteredException;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleMapper;
import com.weg.WEGpark.park.internal.domain.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleResponseDTO;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleMapper vehicleMapper;

    private final VehicleRepository vehicleRepository;

    @Transactional
    public CreateVehicleResponseDTO registerVehicle (CreateVehicleRequestDTO request) {
        Optional<Vehicle> findedVehicle = vehicleRepository.findByPlate(request.plate());
        if (findedVehicle.isEmpty()) {
            Vehicle vehicle = vehicleMapper.toEntity(request);

            vehicleRepository.save(vehicle);

            return vehicleMapper.toResponse(vehicle);
        }
        throw new VehicleAlreadyRegisteredException
                ("This vehicle is already registeres, try to vinculater with the owner, or dismiss");
    }
}
