package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.rh.DesactivateAndActivateUserEvent;
import com.weg.WEGpark.rh.FindParkUserEvent;
import com.weg.WEGpark.rh.GetParkUsersEvent;
import com.weg.WEGpark.rh.internal.app.mapper.UserOperationMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.dto.rh.GetRhResponseDTO;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import com.weg.WEGpark.shared.exception.NotFoundException;
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
    private final RhRepository rhRepository;
    private final OperationService operationService;

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

    public Record findUser (UUID userUuid, RolesType role) {
        return switch (role) {
            case ROLE_RH -> rhService.findUserByUuid(userUuid);
            case ROLE_PARK, ROLE_GUARD -> {
                CompletableFuture<Record> eventResponse = new CompletableFuture<>();
                applicationEventPublisher.publishEvent(new FindParkUserEvent(eventResponse, userUuid));
                yield eventResponse.join();
            }
            case ROLE_ADMIN -> throw new NotFoundException("Admin users do not have an rh or park profile");
        };
    }

    @Transactional
    public UpdateUserResponseDTO updateUserAuthData (UpdateUserRequestDTO request, UUID targetUuid, JWTUserData jwtUserData) {
        CompletableFuture<UpdateUserResponseDTO> eventResponse = new CompletableFuture<>();

        applicationEventPublisher.publishEvent(userOperationMapper.toUpdateAuthEvent(request, eventResponse, targetUuid));

        UpdateUserResponseDTO response = eventResponse.join();

        operationService.saveOperation(jwtUserData, response.id(), OperationType.UPDATE);

        return response;
    }


    @Transactional
    public void desactivateAndActivateUser (UUID uuid, JWTUserData jwtUserData) {
        CompletableFuture<Long> userIdResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new DesactivateAndActivateUserEvent(userIdResponse, uuid));
        Long userId = userIdResponse.join();
        operationService.saveOperation(jwtUserData, userId, OperationType.DESACTIVATE);
    }
}
