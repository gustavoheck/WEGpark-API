package com.weg.WEGpark.auth.internal.infra.repository;

import com.weg.WEGpark.auth.internal.domain.model.NumberToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NumberTokenRepository extends JpaRepository<NumberToken, UUID> {
}
