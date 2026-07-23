package com.weg.WEGpark.auth.internal.infra.repository;

import com.weg.WEGpark.auth.internal.domain.enums.RolesType;
import com.weg.WEGpark.auth.internal.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmail (String email);

    Optional<User> findByEmailAndRole (String email, RolesType role);
}
