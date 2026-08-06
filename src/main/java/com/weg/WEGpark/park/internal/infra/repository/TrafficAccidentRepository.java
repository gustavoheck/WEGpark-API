package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrafficAccidentRepository extends JpaRepository<TrafficAccident, Long> {

    Optional<TrafficAccident> findByUuid(UUID uuid);
}
