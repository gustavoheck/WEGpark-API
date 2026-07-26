package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GuardRepository extends JpaRepository<Guard, Long> {

    Optional<Guard> findByUuid (UUID uuid);
}
