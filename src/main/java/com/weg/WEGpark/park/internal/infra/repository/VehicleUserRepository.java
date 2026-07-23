package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleUserRepository extends JpaRepository<VehicleUser, Long> {

    Optional<VehicleUser> findByParkUserId (Long id);
}
