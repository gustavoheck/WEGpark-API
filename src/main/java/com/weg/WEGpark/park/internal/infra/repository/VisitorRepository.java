package com.weg.WEGpark.park.internal.infra.repository;

import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Page<Visitor> findVisitorByCpf (String cpf, Pageable page);

    Optional<Visitor> findByUuid (UUID uuid);
}
