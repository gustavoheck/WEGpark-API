package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.rh.DesactivateAndActivateUserEvent;
import com.weg.WEGpark.rh.internal.app.mapper.UserOperationMapper;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserOperationService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserOperationMapper userOperationMapper;

    public Page<Object> listObject (FindUserFilter filter) {

    }

    @Transactional
    public UpdateUserResponseDTO updateUser (UpdateUserRequestDTO request, UUID targetUuid) {
        CompletableFuture<UpdateUserResponseDTO> eventResponse = new CompletableFuture<>();

        applicationEventPublisher.publishEvent(userOperationMapper.toUpdateAuthEvent(request, eventResponse, targetUuid));

        eventResponse.thenApply(response -> response);
        return null;
    }

    @Transactional
    public void desactivateAndActivateUser (UUID uuid) {
        applicationEventPublisher.publishEvent(new DesactivateAndActivateUserEvent(uuid));
    }
}
