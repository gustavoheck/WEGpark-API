package com.weg.WEGpark.auth.internal.infra.repository;

import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.auth.internal.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmail (String email);

    Optional<User> findByUuid (UUID uuid);

    Optional<User> findByEmailAndRole (String email, RolesType role);
}
