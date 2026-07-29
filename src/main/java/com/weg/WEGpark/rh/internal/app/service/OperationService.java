package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.domain.model.Operation;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.infra.repository.OperationRepository;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationService {

    private final RhRepository rhRepository;
    private final OperationRepository operationRepository;

    public void saveOperation (JWTUserData jwtUserData, UUID uuid, OperationType opType) {
        if (!jwtUserData.roles().getFirst().equals(RolesType.ROLE_ADMIN.toString())) {
            Rh rh = rhRepository.findByUuid(jwtUserData.uuid())
                    .orElseThrow(() -> new NotFoundException("Any rh was found by the logged uuid"));
            Operation operation = new Operation(opType, uuid);
            operation.setRh(rh);
            operationRepository.save(operation);
        }
    }
}
