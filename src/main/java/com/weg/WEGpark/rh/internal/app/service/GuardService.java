package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.GuardRegisteredEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.GuardParkRegisteredEvent;
import com.weg.WEGpark.rh.internal.app.mapper.RhEventMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.domain.model.Operation;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.dto.guard.RegisterGuardRequestDTO;
import com.weg.WEGpark.rh.internal.dto.guard.RegisterGuardResponseDTO;
import com.weg.WEGpark.rh.internal.infra.repository.OperationRepository;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuardService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final RhEventMapper rhEventMapper;
    private final RhRepository rhRepository;
    private final OperationRepository operationRepository;

    public RegisterGuardResponseDTO createGuard (RegisterGuardRequestDTO request, JWTUserData jwtUserData) {
        CompletableFuture<GuardParkRegisteredEvent> guardRegisteredEvent = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(rhEventMapper.toGuardRegisterEvent(request, guardRegisteredEvent));

        guardRegisteredEvent.thenApply( event -> {
            Rh rh = rhRepository.findByUuid(jwtUserData.uuid())
                    .orElseThrow(() -> new NotFoundException("Any user was found by the logged uuid"));
            Operation operation = new Operation(OperationType.CREATE, event.uuid());
            operationRepository.save(operation);
            return rhEventMapper.toGuardRegisterResponse(event);
        });
        return null;
    }
}
