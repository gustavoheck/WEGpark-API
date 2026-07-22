package com.weg.WEGpark.auth.internal.infra.repository;

import com.weg.WEGpark.auth.internal.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail (String email);

    Boolean existsByEmail (String email);
}
