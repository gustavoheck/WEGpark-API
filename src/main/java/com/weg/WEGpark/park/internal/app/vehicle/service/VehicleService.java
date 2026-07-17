package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyRegisteredException;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.CreateVehicleMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.GetVehicleMapper;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.GetVehicleResponseDTO;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final CreateVehicleMapper createVehicleMapper;
    private final GetVehicleMapper getVehicleMapper;

    private final VehicleRepository vehicleRepository;

    @Transactional
    public CreateVehicleResponseDTO registerVehicle (CreateVehicleRequestDTO request) {
        Optional<Vehicle> findedVehicle = vehicleRepository.findByPlate(request.plate());
        if (findedVehicle.isEmpty()) {
            Vehicle vehicle = createVehicleMapper.toEntity(request);

            vehicleRepository.save(vehicle);

            return createVehicleMapper.toResponse(vehicle);
        }
        throw new VehicleAlreadyRegisteredException
                ("This vehicle is already registered, try to vinculate with the owner, or dismiss");
    }

    public List<GetVehicleResponseDTO> findAllVehicle () {
        List<Vehicle> vehicleList = vehicleRepository.findAll();

        return vehicleList
                .stream()
                .map(getVehicleMapper::toResponse)
                .toList();
    }
}
