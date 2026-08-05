package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.DefaultRegisteredEvent;
import com.weg.WEGpark.auth.GetUsersActiveEvent;
import com.weg.WEGpark.auth.shared.exception.AlreadyHaveAccountException;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.rh.GetRhUserIdEvent;
import com.weg.WEGpark.rh.GetRhUserNameEvent;
import com.weg.WEGpark.rh.UserSearchResult;
import com.weg.WEGpark.rh.internal.app.mapper.RhMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.dto.rh.*;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
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
import java.util.Map;
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

        return findUserByUuid(jwtUserData.uuid());
    }

    public GetRhResponseDTO findUserByUuid(UUID userUuid) {
        Rh rh = rhRepository.findByUuid(userUuid)
                .orElseThrow(() -> new NotFoundException("Any rh account was found by %s uuid".formatted(userUuid)));

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

    public Page<UserSearchResult> listRhUsers (FindUserFilter findUserFilter, Pageable pageable) {
        if (findUserFilter != null && !FilterUtil.checkMoreThanOneFilter(findUserFilter)) {
            throw new MoreThenOneFilterException("You can not use more than one filter");
        }

        if (!FilterUtil.checkHaveFilter(findUserFilter)) {
            return rhRepository.findAll(pageable)
                    .map(rh -> toSearchResult(rh, null));
        }

        if (findUserFilter.active() == null) {
            return Page.empty(pageable);
        }

        List<Rh> rhUsers = rhRepository.findAll(pageable.getSort());
        Map<Long, Boolean> activeByUserId = getUsersActive(rhUsers);

        List<UserSearchResult> responseList = rhUsers
                .stream()
                .filter(user -> findUserFilter.active().equals(activeByUserId.get(user.getId())))
                .map(user -> toSearchResult(user, activeByUserId.get(user.getId())))
                .toList();

        return toPage(responseList, pageable);
    }

    private UserSearchResult toSearchResult(Rh rh, Boolean active) {
        return new UserSearchResult(
                rh.getId(),
                rh.getUuid(),
                rh.getEmail(),
                rh.getTelephone(),
                rh.getName(),
                rh.getBadgeNumber(),
                null,
                active,
                rhMapper.toGetResponse(rh)
        );
    }

    private Map<Long, Boolean> getUsersActive(List<Rh> rhUsers) {
        if (rhUsers.isEmpty()) {
            return Map.of();
        }

        List<Long> userIds = rhUsers
                .stream()
                .map(Rh::getId)
                .distinct()
                .toList();
        CompletableFuture<Map<Long, Boolean>> eventResponse = new CompletableFuture<>();

        applicationEventPublisher.publishEvent(new GetUsersActiveEvent(eventResponse, userIds));

        return eventResponse.join();
    }

    private Page<UserSearchResult> toPage(List<UserSearchResult> content, Pageable pageable) {
        if (pageable.isUnpaged()) {
            return new PageImpl<>(content);
        }

        long offset = pageable.getOffset();
        if (offset >= content.size()) {
            return new PageImpl<>(List.of(), pageable, content.size());
        }

        int fromIndex = (int) offset;
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), content.size());

        return new PageImpl<>(content.subList(fromIndex, toIndex), pageable, content.size());
    }

    private void validateRhRole(JWTUserData jwtUserData) {
        if (!jwtUserData.roles().contains(RolesType.ROLE_RH.name())) {
            throw new AccessDeniedException("Only Rh users can access Rh profile data");
        }
    }
}
