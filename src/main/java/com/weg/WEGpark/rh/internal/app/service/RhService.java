package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.DefaultRegisteredEvent;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.rh.internal.app.mapper.RhMapper;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.dto.rh.RegisterRhRequestDTO;
import com.weg.WEGpark.rh.internal.dto.rh.RegisterRhResponseDTO;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RhService {

    private final RhMapper rhMapper;
    private final RhRepository rhRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public RegisterRhResponseDTO registerRh (RegisterRhRequestDTO request) {
        CompletableFuture<DefaultRegisteredEvent> eventResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(rhMapper.toRegisterEvent(request.defaults(), eventResponse));
        eventResponse.thenApply(response -> {
            Rh rh = rhMapper.toEntity(request);
            rh.setId(response.id());
            rh.setUuid(response.uuid());
            rh.setEmail(response.email());
            return rhMapper.toRegisterResponse(rh);
        });
        return null;
    }
}
