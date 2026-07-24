package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.park.FindAssociationNotificationEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.ParkUserMapper;
import com.weg.WEGpark.park.internal.app.vehicle.exception.NotificationNotFoundException;
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
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleMapper;
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
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleMapper vehicleMapper;
    private final ParkUserMapper parkUserMapper;

    private final VehicleRepository vehicleRepository;
    private final VehicleUserRepository vehicleUserRepository;
    private final ParkUserRepository parkUserRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final EventMapper eventMapper;

    @Transactional
    public CreateVehicleResponseDTO registerVehicle(CreateVehicleRequestDTO request, UserDetails userDetails) {
        Optional<Vehicle> findedVehicle = vehicleRepository.findByPlate(request.plate());
        if (findedVehicle.isEmpty()) {
            ParkUser loggedUser = parkUserRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new NotFoundException("Any park user was found by the logged email"));

            Optional<VehicleUser> possibleUser = vehicleUserRepository.findByParkUserId(loggedUser.getId());
            if (possibleUser.isPresent()) {
                possibleUser.get().setActive(true);
                possibleUser.get().setProprietary(true);
            } else {

                Vehicle vehicle = vehicleMapper.toEntity(request);

                String plate = vehicle.getPlate();
                plate = plate.toUpperCase().replace("-", "").trim();
                vehicle.setPlate(plate);

                if (vehicleRepository.existsByPlate(vehicle.getPlate()))
                    throw new VehicleAlreadyRegisteredException
                            ("A vehicle with the %s plate is already registered".formatted(vehicle.getPlate()));

                vehicleRepository.saveAndFlush(vehicle);

                VehicleUser vehicleUser = new VehicleUser(loggedUser, vehicle);
                vehicleUser.setProprietary(true);

                vehicleUserRepository.save(vehicleUser);

                return vehicleMapper.toCreateResponse(vehicle);
            }
        }
        throw new VehicleAlreadyRegisteredException
                ("This vehicle is already registered, try to vinculate with the owner, or dismiss");
    }

    @Transactional
    public AssociateWithVehicleResponseDTO associateToRegisteredVehicle(UUID uuidNotification) {

        CompletableFuture<FindAssociationNotificationResponse> eventResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new FindAssociationNotificationEvent(eventResponse, uuidNotification));

        Vehicle vehicleToAssociate;
        ParkUser userToAssociate;

        try {
            userToAssociate = parkUserRepository.findById(eventResponse.get().idUserToAssociate())
                    .orElseThrow(() -> new NotFoundException("Any park user was found by the logged email"));

            Optional<VehicleUser> possibleUser = vehicleUserRepository.findByParkUserId(userToAssociate.getId());
            if (possibleUser.isPresent()) {
                possibleUser.get().setActive(true);
                possibleUser.get().setProprietary(true);
            } else {
                vehicleToAssociate = vehicleRepository.findById(eventResponse.get().idVehicleToAssociate())
                        .orElseThrow(() -> new NotFoundException("Any vehicle with this id was found"));

                VehicleUser vehicleUser = new VehicleUser(userToAssociate, vehicleToAssociate);
                vehicleUser.setProprietary(false);
                vehicleUserRepository.save(vehicleUser);
            }
        } catch (Exception e) {
            throw new NotificationNotFoundException("Error trying to find association notification");
        }

        return parkUserMapper.toAssociationResponse(userToAssociate);
    }

    public void SendNotificationForAssociate(AssociationNotificationRequestDTO request, UserDetails userDetails) {
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

    public List<GetVehicleResponseDTO> findVehicle(FilterVehicleRequestDTO filter) {
        if (FilterUtil.checkMoreThanOneFilter(filter)) {
            String plate = null;

            if (filter.plate() != null && !filter.plate().isBlank()) {
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
                    .map(vehicleMapper::toGetResponse)
                    .toList();
        }
        throw new MoreThenOneFilterException("You can not use more than one filter");
    }

    @Transactional
    public GetVehicleResponseDTO updateVehicle(UUID uuid, UpdateVehicleRequestDTO request) {

        Vehicle vehicle = vehicleRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException(("The vehicle was not found by %s uuid".formatted(uuid))));

        vehicleMapper.updateFromDto(request, vehicle);

        vehicleRepository.save(vehicle);

        return vehicleMapper.toGetResponse(vehicle);
    }
}
