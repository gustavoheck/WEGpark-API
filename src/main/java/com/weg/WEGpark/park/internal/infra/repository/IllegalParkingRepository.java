package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IllegalParkingRepository extends JpaRepository<IllegalParking, Long> {

    Optional<IllegalParking> findByUuid(UUID uuid);
}
