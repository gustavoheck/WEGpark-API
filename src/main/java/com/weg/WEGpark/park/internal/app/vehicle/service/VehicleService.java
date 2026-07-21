package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.park.internal.app.shared.util.FilterUtil;
import com.weg.WEGpark.park.internal.app.vehicle.exception.MoreThenOneFilterException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyRegisteredException;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.CreateVehicleMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.GetVehicleMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.UpdateVehicleMapper;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.CreateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.CreateVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.GetVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.UpdateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.filter.FilterVehicleRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import com.weg.WEGpark.park.internal.infra.specification.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final CreateVehicleMapper createVehicleMapper;
    private final GetVehicleMapper getVehicleMapper;
    private final UpdateVehicleMapper updateVehicleMapper;

    private final VehicleRepository vehicleRepository;

    @Transactional
    public CreateVehicleResponseDTO registerVehicle (CreateVehicleRequestDTO request) {
        Optional<Vehicle> findedVehicle = vehicleRepository.findByPlate(request.plate());
        if (findedVehicle.isEmpty()) {
            Vehicle vehicle = createVehicleMapper.toEntity(request);
            // Need to add the plate formation here
            vehicleRepository.save(vehicle);

            return createVehicleMapper.toResponse(vehicle);
        }
        throw new VehicleAlreadyRegisteredException
                ("This vehicle is already registered, try to vinculate with the owner, or dismiss");
    }

    public List<GetVehicleResponseDTO> findVehicle (FilterVehicleRequestDTO filter) {
        if (FilterUtil.checkMoreThanOneFilter(filter)) {
            String plate = null;

            if (filter.plate() != null && !filter.plate().isBlank()){
                plate = filter.plate().toUpperCase().replace("-", "").trim();
            }

            Specification<Vehicle> spec = Specification
                    .where(VehicleSpecification.hasPlate(plate))
                    .and(VehicleSpecification.hasModel(filter.model()))
                    .and(VehicleSpecification.hasBrand(filter.brand()))
                    .and(VehicleSpecification.hasColor(filter.color()))
                    .and(VehicleSpecification.belongsToUser(filter.userName()));

            List<Vehicle> vehicleList = vehicleRepository.findAll(spec);

            return vehicleList
                    .stream()
                    .map(getVehicleMapper::toResponse)
                    .toList();
        }
        throw new MoreThenOneFilterException("You can not use more than one filter");
    }

    @Transactional
    public GetVehicleResponseDTO updateVehicle(UUID uuid, UpdateVehicleRequestDTO request) {

        Vehicle vehicle = vehicleRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException(("The vehicle was not found by %s uuid".formatted(uuid))));

        updateVehicleMapper.updateFromDto(request, vehicle);

        vehicleRepository.save(vehicle);

        return getVehicleMapper.toResponse(vehicle);
    }
}
