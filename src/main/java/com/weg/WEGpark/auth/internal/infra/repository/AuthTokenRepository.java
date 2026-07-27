package com.weg.WEGpark.auth.internal.infra.repository;

import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {
}
