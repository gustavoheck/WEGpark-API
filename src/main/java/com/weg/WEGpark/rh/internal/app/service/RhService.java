package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.DefaultRegisteredEvent;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.rh.internal.app.mapper.RhMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.domain.model.Operation;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.dto.rh.RegisterRhRequestDTO;
import com.weg.WEGpark.rh.internal.dto.rh.RegisterRhResponseDTO;
import com.weg.WEGpark.rh.internal.dto.rh.UpdateRhRequestDTO;
import com.weg.WEGpark.rh.internal.infra.repository.OperationRepository;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RhService {

    private final RhMapper rhMapper;
    private final RhRepository rhRepository;
    private final OperationRepository operationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public RegisterRhResponseDTO registerRh (RegisterRhRequestDTO request) {
        CompletableFuture<DefaultRegisteredEvent> eventResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(rhMapper.toRegisterEvent(request.defaults(), eventResponse));
        eventResponse.thenApply(response -> {
            Rh rh = rhMapper.toEntity(request);
            rh.setId(response.id());
            rh.setUuid(response.uuid());
            rh.setEmail(response.email());
            Operation operation = new Operation(OperationType.CREATE, response.uuid());
            operationRepository.save(operation);
            return rhMapper.toRegisterResponse(rh);
        });
        return null;
    }

    public UpdateRhRequestDTO updateRh (UpdateRhRequestDTO request, UUID uuid) {
        Rh rh = rhRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Any rh account was found by %s uuid".formatted(uuid)));

        rhMapper.updateFromDTO(request, rh);

        rhRepository.save(rh);

        Operation operation = new Operation(OperationType.UPDATE, uuid);
        operationRepository.save(operation);

        return rhMapper.toUpdateResponse(rh);
    }
}
