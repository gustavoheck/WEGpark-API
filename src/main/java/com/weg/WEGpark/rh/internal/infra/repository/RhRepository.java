package com.weg.WEGpark.rh.internal.infra.repository;

import com.weg.WEGpark.rh.internal.domain.model.Rh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RhRepository extends JpaRepository<Rh, Long> {

    Optional<Rh> findByUuid (UUID uuid);
}
