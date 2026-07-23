package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.park.internal.app.vehicle.mapper.EventMapper;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociateWithVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociationNotificationRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.*;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleUserRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.park.internal.app.shared.util.FilterUtil;
import com.weg.WEGpark.park.internal.app.vehicle.exception.MoreThenOneFilterException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyRegisteredException;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.CreateVehicleMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.GetVehicleMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.UpdateVehicleMapper;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.filter.FilterVehicleRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import com.weg.WEGpark.park.internal.infra.specification.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final VehicleUserRepository vehicleUserRepository;
    private final ParkUserRepository parkUserRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final EventMapper eventMapper;

    @Transactional
    public CreateVehicleResponseDTO registerVehicle (CreateVehicleRequestDTO request, UserDetails userDetails) {
        Optional<Vehicle> findedVehicle = vehicleRepository.findByPlate(request.plate());
        if (findedVehicle.isEmpty()) {
            Vehicle vehicle = createVehicleMapper.toEntity(request);

            String plate = vehicle.getPlate();
            plate = plate.toUpperCase().replace("-", "").trim();
            vehicle.setPlate(plate);

            if (vehicleRepository.existsByPlate(vehicle.getPlate()))
                throw new VehicleAlreadyRegisteredException
                        ("A vehicle with the %s plate is already registered".formatted(vehicle.getPlate()));

            vehicleRepository.saveAndFlush(vehicle);
            ParkUser loggedUser = parkUserRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new NotFoundException("Any park user was found by the logged email"));

            VehicleUser vehicleUser = new VehicleUser(loggedUser, vehicle);
            vehicleUser.setProprietary(true);

            vehicleUserRepository.save(vehicleUser);

            return createVehicleMapper.toResponse(vehicle);
        }
        throw new VehicleAlreadyRegisteredException
                ("This vehicle is already registered, try to vinculate with the owner, or dismiss");
    }

    @Transactional // Need refactoring
    public AssociateWithVehicleResponseDTO associateToRegisteredVehicle
            (AssociateWithVehicleRequestDTO request, UserDetails userDetails) {
        Vehicle vehicleToAssociate = vehicleRepository.findByPlate(request.plate())
                .orElseThrow(() -> new NotFoundException("Any vehicle with the %s plate was found".formatted(request.plate())));
        ParkUser userToAssociate = parkUserRepository.findByUuid(UUID.fromString(request.uuid()))
                .orElseThrow(() -> new NotFoundException("Any park user was found by the logged email"));

        VehicleUser vehicleUser = new VehicleUser(loggedUser, vehicleToAssociate);
        vehicleUser.setProprietary(false);

        vehicleUserRepository.save(vehicleUser);

        return createVehicleMapper.toResponse(vehicleToAssociate);
    }

    public void SendNotificationForAssociate (AssociationNotificationRequestDTO request, UserDetails userDetails) {
        ParkUser loggedUser = parkUserRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Any park user was found by the logged email"));
        Vehicle vehicle = vehicleRepository.findByPlate(request.plate())
                        .orElseThrow(() -> new NotFoundException("Any vehicle was found by %s plate".formatted(request.plate())));
        ParkUser vehicleOwner = vehicle
                .getParkUsers()
                .stream()
                .filter(vehicleUser -> vehicleUser.getProprietary())
                .toList()
                .getFirst()
                .getParkUser();
        applicationEventPublisher.publishEvent(eventMapper.toEvent(loggedUser, vehicle, vehicleOwner));
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
