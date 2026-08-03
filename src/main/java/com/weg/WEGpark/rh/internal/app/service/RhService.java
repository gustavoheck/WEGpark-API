package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.DefaultRegisteredEvent;
import com.weg.WEGpark.auth.shared.exception.AlreadyHaveAccountException;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.rh.GetRhUserIdEvent;
import com.weg.WEGpark.rh.GetRhUserNameEvent;
import com.weg.WEGpark.rh.internal.app.mapper.RhMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.dto.rh.*;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import com.weg.WEGpark.shared.exception.MoreThenOneFilterException;
import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.shared.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RhService {

    private final RhMapper rhMapper;
    private final RhRepository rhRepository;
    private final OperationService operationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public RegisterRhResponseDTO registerRh (RegisterRhRequestDTO request, JWTUserData jwtUserData) {
        CompletableFuture<DefaultRegisteredEvent> eventResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(rhMapper.toRegisterEvent(request.defaults(), eventResponse));
        DefaultRegisteredEvent response = eventResponse.join();

        if (!rhRepository.existsByEmail(request.defaults().email())) {
            Rh rh = rhMapper.toEntity(request);
            rh.setId(response.id());
            rh.setUuid(response.uuid());
            rh.setEmail(response.email());
            rhRepository.save(rh);
            operationService.saveOperation(jwtUserData, response.id(), OperationType.CREATE);
            return rhMapper.toRegisterResponse(rh);
        } else {
            throw new AlreadyHaveAccountException("An rh account with this email is already registered");
        }
    }

    @Transactional
    public UpdateRhResponseDTO updateRh (UpdateRhRequestDTO request, UUID uuid, JWTUserData jwtUserData) {
        Rh rh = rhRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Any rh account was found by %s uuid".formatted(uuid)));

        rhMapper.updateFromDTO(request, rh);

        rhRepository.save(rh);

        operationService.saveOperation(jwtUserData, rh.getId(), OperationType.UPDATE);
        return rhMapper.toUpdateResponse(rh);
    }

    @Transactional
    public UpdateRhResponseDTO updateMyProfile(UpdateRhRequestDTO request, JWTUserData jwtUserData) {
        validateRhRole(jwtUserData);
        return updateRh(request, jwtUserData.uuid(), jwtUserData);
    }

    public GetRhResponseDTO findMyProfile(JWTUserData jwtUserData) {
        validateRhRole(jwtUserData);

        Rh rh = rhRepository.findByUuid(jwtUserData.uuid())
                .orElseThrow(() -> new NotFoundException("Any rh account was found by %s uuid".formatted(jwtUserData.uuid())));

        return rhMapper.toGetResponse(rh);
    }

    public void getUserName(GetRhUserNameEvent event) {
        Rh rh = rhRepository.findByUuid(event.userUuid())
                .orElseThrow(() -> new NotFoundException("Any rh account was found by %s uuid".formatted(event.userUuid())));

        event.eventResponse().complete(rh.getName());
    }

    public void getUserId(GetRhUserIdEvent event) {
        Rh rh = rhRepository.findByUuid(event.userUuid())
                .orElseThrow(() -> new NotFoundException("Any rh account was found by %s uuid".formatted(event.userUuid())));

        event.eventResponse().complete(rh.getId());
    }

    public Page<GetRhResponseDTO> listRhUsers (FindUserFilter findUserFilter, Pageable pageable) {
        if (FilterUtil.checkMoreThanOneFilter(findUserFilter)) {
            if (FilterUtil.checkHaveFilter(findUserFilter)) {
                if (findUserFilter.active() != null) {
                    List<GetRhResponseDTO> responseList;
                    responseList = rhRepository
                            .findAll()
                            .stream()
                            .filter(user -> getUserActive(user.getUuid()) == findUserFilter.active())
                            .map(rhMapper::toGetResponse)
                            .toList();
                    return new PageImpl<>(responseList, pageable, responseList.size());
                }
                return Page.empty();
            }
            return rhRepository.findAll(pageable)
                    .map(rhMapper::toGetResponse);
        }
        throw new MoreThenOneFilterException("You can not use more than one filter");
    }

    private Boolean getUserActive (UUID targetUuid) {
        CompletableFuture<Boolean> isUserActive = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new IsParkUserActiveEvent(isUserActive, targetUuid));
        return isUserActive.join();
    }

    private void validateRhRole(JWTUserData jwtUserData) {
        if (!jwtUserData.roles().contains(RolesType.ROLE_RH.name())) {
            throw new AccessDeniedException("Only Rh users can access Rh profile data");
        }
    }
}
