package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.park.internal.app.vehicle.exception.MoreThenOneFilterException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyRegisteredException;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.CreateVehicleMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.GetVehicleMapper;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.FilterVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.GetVehicleResponseDTO;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import com.weg.WEGpark.park.internal.infra.specification.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

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
            // Need to add the plate formation here
            vehicleRepository.save(vehicle);

            return createVehicleMapper.toResponse(vehicle);
        }
        throw new VehicleAlreadyRegisteredException
                ("This vehicle is already registered, try to vinculate with the owner, or dismiss");
    }

    public List<GetVehicleResponseDTO> findVehicle (FilterVehicleRequestDTO filter) {
        long nonNullCamps = Stream.of(
                        filter.plate(),
                        filter.model(),
                        filter.brand(),
                        filter.color(),
                        filter.userName()
                )
                .filter(Objects::nonNull)
                .filter(camp -> !(camp.isBlank()))
                .count();

        if (nonNullCamps <= 1 ) {
            String plate = null;

            if (filter.plate() != null && !filter.plate().isBlank()){
                plate = filter.plate().toUpperCase().replace("-", "").trim();
            }

            Specification<Vehicle> spec = Specification.where(VehicleSpecification.hasPlate(plate));
            spec = spec.and(VehicleSpecification.hasModel(filter.model()))
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
}
