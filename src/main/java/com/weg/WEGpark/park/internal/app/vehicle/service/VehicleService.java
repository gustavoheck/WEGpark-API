package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.park.FindAssociationNotificationEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.ParkUserMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VehicleUserMapper;
import com.weg.WEGpark.park.internal.app.vehicle.exception.NotificationNotFoundException;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleEventMapper;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociateWithVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociationNotificationRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.*;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleUserRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.shared.util.FilterUtil;
import com.weg.WEGpark.shared.exception.MoreThenOneFilterException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyRegisteredException;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleMapper;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.filter.FilterVehicleRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import com.weg.WEGpark.park.internal.infra.specification.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final VehicleEventMapper vehicleEventMapper;
    private final VehicleUserMapper vehicleUserMapper;

    @Transactional
    public GetVehicleResponseDTO registerVehicle(CreateVehicleRequestDTO request, JWTUserData userData) {
        Optional<VehicleUser> alreadyAssociatedVehicleUser =
                vehicleUserRepository.findByVehiclePlateAndParkUserUuid(request.plate(), userData.uuid());
        Boolean alreadyExistentOwner =
                vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive(request.plate(), true, true);
        if (alreadyAssociatedVehicleUser.isPresent() && alreadyExistentOwner == false) {
            VehicleUser loggedVehicleUser = alreadyAssociatedVehicleUser.get();
            loggedVehicleUser.setActive(true);
            loggedVehicleUser.setVehicleOwner(true);
            vehicleUserRepository.save(loggedVehicleUser);

        } else if (vehicleRepository.existsByPlate(request.plate()) == false) {
            ParkUser loggedUser = parkUserRepository.findByUuid(userData.uuid())
                    .orElseThrow(() -> new NotFoundException("Any park user was found by the logged uuid"));

            Vehicle vehicle = vehicleMapper.toEntity(request);

            String plate = vehicle.getPlate();
            plate = plate.toUpperCase().replace("-", "").trim();
            vehicle.setPlate(plate);

            vehicleRepository.saveAndFlush(vehicle);

            VehicleUser vehicleUser = new VehicleUser(loggedUser, vehicle);
            vehicleUser.setVehicleOwner(true);

            vehicleUserRepository.save(vehicleUser);

            List<GetVehicleUserResponseDTO> userResponseList = vehicle
                    .getParkUsers()
                    .stream()
                    .map(vehicleUserMapper::toResponse)
                    .toList();

            return vehicleMapper.toGetResponse(vehicle, userResponseList);
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
                    .orElseThrow(() -> new NotFoundException("Any park user was found by the logged uuid"));

            Optional<VehicleUser> possibleUser = vehicleUserRepository.findByParkUserId(userToAssociate.getId());
            if (possibleUser.isPresent()) {
                possibleUser.get().setActive(true);
                possibleUser.get().setVehicleOwner(true);
            } else {
                vehicleToAssociate = vehicleRepository.findById(eventResponse.get().idVehicleToAssociate())
                        .orElseThrow(() -> new NotFoundException("Any vehicle with this id was found"));

                VehicleUser vehicleUser = new VehicleUser(userToAssociate, vehicleToAssociate);
                vehicleUser.setVehicleOwner(false);
                vehicleUserRepository.save(vehicleUser);
            }
        } catch (Exception e) {
            throw new NotificationNotFoundException("Error trying to find association notification");
        }

        return parkUserMapper.toAssociationResponse(userToAssociate);
    }

    @Transactional
    public void SendNotificationForAssociate(AssociationNotificationRequestDTO request, JWTUserData userData) {
        ParkUser loggedUser = parkUserRepository.findByUuid(userData.uuid())
                .orElseThrow(() -> new NotFoundException("Any park user was found by the logged email"));
        Vehicle vehicle = vehicleRepository.findByPlate(request.plate())
                .orElseThrow(() -> new NotFoundException("Any vehicle was found by %s plate".formatted(request.plate())));
        ParkUser vehicleOwner = vehicle
                .getParkUsers()
                .stream()
                .filter(vehicleUser -> vehicleUser.getVehicleOwner())
                .toList()
                .getFirst()
                .getParkUser();
        applicationEventPublisher.publishEvent(vehicleEventMapper.toEvent(loggedUser, vehicle, vehicleOwner));
    }

    public Page<GetVehicleResponseDTO> findVehicle(FilterVehicleRequestDTO filter, Pageable pageable) {
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

            Page<Vehicle> vehiclePage = vehicleRepository.findAll(spec, pageable);

            return vehiclePage
                    .map(vehicle -> {
                        List<GetVehicleUserResponseDTO> userResponseList = vehicle
                                .getParkUsers()
                                .stream()
                                .map(vehicleUserMapper::toResponse)
                                .toList();

                        return vehicleMapper.toGetResponse(vehicle, userResponseList);
                    });
        }
        throw new MoreThenOneFilterException("You can not use more than one filter");
    }

    public List<GetVehicleResponseDTO> findMyVehicles (JWTUserData userData) {
        List<VehicleUser> myAssociateVehicles = vehicleUserRepository.findByUuidParkUser(userData.uuid());

        return myAssociateVehicles
                .stream()
                .map(vehicleUser -> {
                    List<GetVehicleUserResponseDTO> userResponseList = vehicleUser.getVehicle()
                            .getParkUsers()
                            .stream()
                            .map(vehicleUserMapper::toResponse)
                            .toList();

                    return vehicleMapper.toGetResponse(vehicleUser.getVehicle(), userResponseList);
                })
                .toList();
    }

    @Transactional
    public UpdateVehicleResponseDTO updateVehicle(UUID uuid, UpdateVehicleRequestDTO request) {

        Vehicle vehicle = vehicleRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException(("The vehicle was not found by %s uuid".formatted(uuid))));

        vehicleMapper.updateFromDto(request, vehicle);

        vehicleRepository.save(vehicle);

        return vehicleMapper.toUpdateResponse(vehicle);
    }
}
