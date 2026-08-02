package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleUserRepository extends JpaRepository<VehicleUser, Long> {

    Optional<VehicleUser> findByVehicleUuidAndParkUserUuid (UUID uuidVehicle, UUID uuidParkUser);

    List<VehicleUser> findByParkUserId (Long id);

    List<VehicleUser> findByUuidParkUser(UUID uuid);

    Optional<VehicleUser> findByVehiclePlateAndParkUserUuid (String plate, UUID uuid);

    Boolean existsByVehiclePlateAndVehicleOwnerAndActive(String plate, Boolean owner, Boolean active);
}
