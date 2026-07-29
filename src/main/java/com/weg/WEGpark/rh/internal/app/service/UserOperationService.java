package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.rh.DesactivateAndActivateUserEvent;
import com.weg.WEGpark.rh.GetParkUsersEvent;
import com.weg.WEGpark.rh.internal.app.mapper.UserOperationMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.domain.model.Operation;
import com.weg.WEGpark.rh.internal.dto.rh.GetRhResponseDTO;
import com.weg.WEGpark.rh.internal.infra.repository.OperationRepository;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import com.weg.WEGpark.shared.exception.MoreThenOneFilterException;
import com.weg.WEGpark.shared.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserOperationService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserOperationMapper userOperationMapper;
    private final RhService rhService;
    private final OperationRepository operationRepository;

    public Page<Record> listUsers (FindUserFilter filter, Pageable pageable) {
        CompletableFuture<Page<Record>> eventResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new GetParkUsersEvent(eventResponse, filter, pageable));
        Page<Record> parkUsersResponse = eventResponse.join();
        Page<GetRhResponseDTO> rhUsersList = rhService.listRhUsers(filter, pageable);

        List<Record> responseList = Stream.of(
                        parkUsersResponse,
                        rhUsersList
                                .map(response -> (Record) response)
                )
                .flatMap(page -> page.getContent().stream())
                .toList();

        long totalElements = parkUsersResponse.getTotalElements() + rhUsersList.getTotalElements();

        return new PageImpl<>(responseList, pageable, totalElements);
    }

    @Transactional
    public UpdateUserResponseDTO updateUserAuthData (UpdateUserRequestDTO request, UUID targetUuid, JWTUserData jwtUserData) {
        CompletableFuture<UpdateUserResponseDTO> eventResponse = new CompletableFuture<>();

        applicationEventPublisher.publishEvent(userOperationMapper.toUpdateAuthEvent(request, eventResponse, targetUuid));

        Operation operation = new Operation(OperationType.UPDATE, jwtUserData.uuid());
        operationRepository.save(operation);

        return eventResponse.join();
    }



    @Transactional
    public void desactivateAndActivateUser (UUID uuid, JWTUserData jwtUserData) {
        applicationEventPublisher.publishEvent(new DesactivateAndActivateUserEvent(uuid));
        Operation operation = new Operation(OperationType.DESACTIVATE, jwtUserData.uuid());
        operationRepository.save(operation);
    }
}
