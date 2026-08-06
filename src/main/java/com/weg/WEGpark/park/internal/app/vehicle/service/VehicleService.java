package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.park.FindAssociationNotificationEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.ParkUserMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VehicleUserMapper;
import com.weg.WEGpark.park.internal.app.vehicle.exception.NotificationNotFoundException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyAssociatedWithUserException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyOwnedByUserException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

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
        String normalizedPlate = normalizePlate(request.plate());

        Optional<VehicleUser> alreadyAssociatedVehicleUser =
                vehicleUserRepository.findByVehiclePlateAndParkUserUuid(normalizedPlate, userData.uuid());
        Boolean alreadyExistentOwner =
                vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive(normalizedPlate, true, true);

        if (alreadyAssociatedVehicleUser.isPresent()) {
            VehicleUser loggedVehicleUser = alreadyAssociatedVehicleUser.get();
            if (loggedVehicleUser.getVehicleOwner()) {
                if (loggedVehicleUser.getActive() && alreadyExistentOwner) {
                    throw vehicleAlreadyOwnedByUserException();
                }
                if (!alreadyExistentOwner) {
                    loggedVehicleUser.setActive(true);
                    vehicleUserRepository.save(loggedVehicleUser);

                    List<GetVehicleUserResponseDTO> loggedUserResponseList = new ArrayList<>();
                    loggedUserResponseList.add(vehicleUserMapper.toResponse(loggedVehicleUser));
                    return vehicleMapper.toGetResponse(loggedVehicleUser.getVehicle(), loggedUserResponseList);
                }
            } else if (loggedVehicleUser.getActive()) {
                throw vehicleAlreadyAssociatedWithUserException();
            }
        }
        if (vehicleRepository.existsByPlate(normalizedPlate) == false) {
            ParkUser loggedUser = parkUserRepository.findByUuid(userData.uuid())
                    .orElseThrow(() -> new NotFoundException("Any park user was found by the logged uuid"));

            Vehicle vehicle = vehicleMapper.toEntity(request);

            vehicle.setPlate(normalizedPlate);

            vehicleRepository.saveAndFlush(vehicle);

            VehicleUser vehicleUser = new VehicleUser(loggedUser, vehicle);
            vehicleUser.setVehicleOwner(true);

            vehicleUserRepository.saveAndFlush(vehicleUser);

            vehicle.getParkUsers().add(vehicleUser);

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
    public AssociateWithVehicleResponseDTO associateToRegisteredVehicle(UUID uuidNotification, JWTUserData jwtUserData) {

        CompletableFuture<FindAssociationNotificationResponse> eventResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new FindAssociationNotificationEvent(
                eventResponse, uuidNotification, jwtUserData.uuid()
        ));

        FindAssociationNotificationResponse associationNotification;

        try {
            associationNotification = eventResponse.orTimeout(8, TimeUnit.SECONDS).join();
        } catch (CompletionException exception) {
            throw new NotificationNotFoundException("Error trying to find association notification");
        }

        ParkUser userToAssociate = parkUserRepository.findById(associationNotification.idUserToAssociate())
                .orElseThrow(() -> new NotFoundException("Any park user was found by the logged uuid"));

        Vehicle vehicleToAssociate = vehicleRepository.findById(associationNotification.idVehicleToAssociate())
                .orElseThrow(() -> new NotFoundException("Any vehicle with this id was found"));

        Optional<VehicleUser> existentAssociation = vehicleUserRepository
                .findByVehicleUuidAndParkUserUuid(vehicleToAssociate.getUuid(), userToAssociate.getUuid());

        if (existentAssociation.isPresent()) {
            VehicleUser vehicleUser = existentAssociation.get();

            if (vehicleUser.getVehicleOwner()) {
                throw vehicleAlreadyOwnedByUserException();
            }

            if (vehicleUser.getActive()) {
                throw vehicleAlreadyAssociatedWithUserException();
            }

            vehicleUser.setActive(true);
            vehicleUserRepository.save(vehicleUser);
        } else {
            VehicleUser vehicleUser = new VehicleUser(userToAssociate, vehicleToAssociate);
            vehicleUser.setVehicleOwner(false);
            vehicleUserRepository.save(vehicleUser);
        }

        return parkUserMapper.toAssociationResponse(userToAssociate);
    }

    @Transactional
    public void SendNotificationForAssociate(AssociationNotificationRequestDTO request, JWTUserData userData) {
        String normalizedPlate = normalizePlate(request.plate());

        ParkUser loggedUser = parkUserRepository.findByUuid(userData.uuid())
                .orElseThrow(() -> new NotFoundException("Any park user was found by the logged email"));
        Vehicle vehicle = vehicleRepository.findByPlate(normalizedPlate)
                .orElseThrow(() -> new NotFoundException("Any vehicle was found by %s plate".formatted(normalizedPlate)));
        Optional<VehicleUser> loggedUserAssociation = vehicleUserRepository
                .findByVehicleUuidAndParkUserUuid(vehicle.getUuid(), loggedUser.getUuid());

        if (loggedUserAssociation.isPresent()) {
            VehicleUser vehicleUser = loggedUserAssociation.get();

            if (vehicleUser.getVehicleOwner()) {
                throw vehicleAlreadyOwnedByUserException();
            }

            if (vehicleUser.getActive()) {
                throw vehicleAlreadyAssociatedWithUserException();
            }
        }

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
            String plate = normalizePlate(filter.plate());

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

    public List<GetVehicleResponseDTO> findMyVehicles(JWTUserData userData) {
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
    public UpdateVehicleResponseDTO updateVehicle(UUID uuid, UpdateVehicleRequestDTO request, JWTUserData jwtUserData) {

        if (!jwtUserData.roles().contains(RolesType.ROLE_GUARD.name())
                && vehicleUserRepository.findByVehicleUuidAndParkUserUuid(uuid, jwtUserData.uuid()).isEmpty()) {
            throw new AccessDeniedException(
                    "Only guards or users associated with the vehicle can update it"
            );
        }
        Vehicle vehicle = vehicleRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException(("The vehicle was not found by %s uuid".formatted(uuid))));

        vehicleMapper.updateFromDto(request, vehicle);

        if (request.plate() != null) {
            vehicle.setPlate(normalizePlate(request.plate()));
        }

        vehicleRepository.save(vehicle);

        return vehicleMapper.toUpdateResponse(vehicle);
    }

    private String normalizePlate(String plate) {
        if (plate == null) {
            return null;
        }

        return plate.toUpperCase().replace("-", "").trim();
    }

    private VehicleAlreadyOwnedByUserException vehicleAlreadyOwnedByUserException() {
        return new VehicleAlreadyOwnedByUserException(
                "This vehicle is already registered by the logged user"
        );
    }

    private VehicleAlreadyAssociatedWithUserException vehicleAlreadyAssociatedWithUserException() {
        return new VehicleAlreadyAssociatedWithUserException(
                "The logged user is already associated with this vehicle"
        );
    }
}
