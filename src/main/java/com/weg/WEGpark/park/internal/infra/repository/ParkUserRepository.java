package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParkUserRepository extends JpaRepository<ParkUser, Long> {

    Boolean existsByEmail(String email);

    Optional<ParkUser> findByEmail(String email);

    Optional<ParkUser> findByUuid (UUID uuid);
}
