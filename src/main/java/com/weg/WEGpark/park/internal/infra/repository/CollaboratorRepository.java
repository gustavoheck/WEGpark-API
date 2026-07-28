package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    Optional<Collaborator> findByBadgeNumberOrEmail (String badgeNumber, String email);

    Page<Collaborator> findByBadgeNumber (String badgeNumber, Pageable pageable);

    Optional<Collaborator> findByUuid (UUID uuid);
}
