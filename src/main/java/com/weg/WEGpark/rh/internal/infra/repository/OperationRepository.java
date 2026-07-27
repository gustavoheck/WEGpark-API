package com.weg.WEGpark.rh.internal.infra.repository;

import com.weg.WEGpark.rh.internal.domain.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}
