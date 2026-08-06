package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WarningRepository extends JpaRepository<Warning, Long> {

    Optional<Warning> findByUuid(UUID uuid);
}
