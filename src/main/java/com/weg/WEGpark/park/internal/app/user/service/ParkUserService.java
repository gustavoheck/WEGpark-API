package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.rh.GetParkUsersEvent;
import com.weg.WEGpark.rh.UserSearchResult;
import com.weg.WEGpark.auth.GetUsersActiveEvent;
import com.weg.WEGpark.park.GetParkUserIdEvent;
import com.weg.WEGpark.park.GetParkUserNameEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import com.weg.WEGpark.shared.util.FilterUtil;
import com.weg.WEGpark.park.internal.app.user.mapper.CollaboratorMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.GuardMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VisitorMapper;
import com.weg.WEGpark.shared.exception.MoreThenOneFilterException;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.internal.infra.repository.CollaboratorRepository;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VisitorRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParkUserService {

    private final ParkUserRepository parkUserRepository;
    private final CollaboratorRepository collaboratorRepository;

    private final VisitorRepository visitorRepository;
    private final VisitorMapper visitorMapper;

    private final CollaboratorMapper collaboratorMapper;
    private final GuardMapper guardMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    public Boolean verifyParkUserToRegister(String email) {
        Boolean existsByEmail = parkUserRepository.existsByEmail(email);

        return existsByEmail;
    }

    public Record findMyProfile(JWTUserData jwtUserData) {
        if (jwtUserData.roles().contains(RolesType.ROLE_RH.name())) {
            throw new AccessDeniedException("Rh users do not have a park user profile");
        }

        ParkUser parkUser = parkUserRepository.findByUuid(jwtUserData.uuid())
                .orElseThrow(() -> new NotFoundException("Any park user was found by %s uuid".formatted(jwtUserData.uuid())));
        Boolean active = getUserActive(parkUser.getUuid());

        return switch (parkUser) {
            case Guard guard -> guardMapper.toGetResponse(guard, active);
            case Collaborator collaborator -> collaboratorMapper.toResponse(collaborator, active);
            case Visitor visitor -> visitorMapper.toResponse(visitor, active);
            default -> throw new NotFoundException("Can not found a user of this type");
        };
    }

    public void getUserName(GetParkUserNameEvent event) {
        ParkUser parkUser = parkUserRepository.findByUuid(event.userUuid())
                .orElseThrow(() -> new NotFoundException("Any park user was found by %s uuid".formatted(event.userUuid())));

        event.eventResponse().complete(parkUser.getName());
    }

    public void getUserId(GetParkUserIdEvent event) {
        ParkUser parkUser = parkUserRepository.findByUuid(event.userUuid())
                .orElseThrow(() -> new NotFoundException("Any park user was found by %s uuid".formatted(event.userUuid())));

        event.eventResponse().complete(parkUser.getId());
    }

    public void findParkUsers(GetParkUsersEvent event, Pageable pageable) {
        var filter = event.findUserFilter();

        if (filter != null && !FilterUtil.checkMoreThanOneFilter(filter)) {
            event.eventResponse().completeExceptionally(
                    new MoreThenOneFilterException("You can not use more than one filter to search for users")
            );
            return;
        }

        Page<UserSearchResult> response;
        if (!FilterUtil.checkHaveFilter(filter)) {
            response = findAllUsers(pageable);
        } else if (hasText(filter.name())) {
            response = findParkUserName(filter.name(), pageable);
        } else if (hasText(filter.badgeNumber())) {
            response = findCollaboratorBadgeNumber(filter.badgeNumber(), pageable);
        } else if (hasText(filter.cpf())) {
            response = findVisitorCpf(filter.cpf(), pageable);
        } else if (filter.active() != null) {
            response = findAllActiveDesactiveUsers(filter.active(), pageable);
        } else {
            response = findAllUsers(pageable);
        }

        event.eventResponse().complete(response);
    }

    private Page<UserSearchResult> findAllUsers(Pageable pageable) {
        Page<ParkUser> parkUsers = parkUserRepository.findAll(pageable);
        Map<Long, Boolean> activeByUserId = getUsersActive(parkUsers.getContent());

        return parkUsers.map(parkUser -> toSearchResult(parkUser, activeByUserId.get(parkUser.getId())));
    }

    private Page<UserSearchResult> findAllActiveDesactiveUsers(Boolean activeFilter, Pageable pageable) {
        List<ParkUser> parkUsers = parkUserRepository.findAll(pageable.getSort());
        Map<Long, Boolean> activeByUserId = getUsersActive(parkUsers);

        List<UserSearchResult> responseList = parkUsers
                .stream()
                .filter(parkUser -> activeFilter.equals(activeByUserId.get(parkUser.getId())))
                .map(parkUser -> toSearchResult(parkUser, activeByUserId.get(parkUser.getId())))
                .toList();

        return toPage(responseList, pageable);
    }

    private Page<UserSearchResult> findParkUserName(String name, Pageable pageable) {
        Page<ParkUser> parkUsers = parkUserRepository.findByNameLike(name, pageable);
        Map<Long, Boolean> activeByUserId = getUsersActive(parkUsers.getContent());

        return parkUsers.map(parkUser -> toSearchResult(parkUser, activeByUserId.get(parkUser.getId())));
    }

    private Page<UserSearchResult> findCollaboratorBadgeNumber(String badgeNumber, Pageable pageable) {
        Page<Collaborator> collaborators = collaboratorRepository.findByBadgeNumber(badgeNumber, pageable);
        Map<Long, Boolean> activeByUserId = getUsersActive(collaborators.getContent());

        return collaborators.map(collaborator -> toSearchResult(collaborator, activeByUserId.get(collaborator.getId())));
    }

    private Page<UserSearchResult> findVisitorCpf(String cpf, Pageable pageable) {
        Page<Visitor> visitors = visitorRepository.findVisitorByCpf(cpf, pageable);
        Map<Long, Boolean> activeByUserId = getUsersActive(visitors.getContent());

        return visitors.map(visitor -> toSearchResult(visitor, activeByUserId.get(visitor.getId())));
    }

    private UserSearchResult toSearchResult(ParkUser parkUser, Boolean active) {
        String badgeNumber = switch (parkUser) {
            case Collaborator collaborator -> collaborator.getBadgeNumber();
            default -> null;
        };
        String cpf = parkUser instanceof Visitor visitor ? visitor.getCpf() : null;

        return new UserSearchResult(
                parkUser.getId(),
                parkUser.getUuid(),
                parkUser.getEmail(),
                parkUser.getTelephone(),
                parkUser.getName(),
                badgeNumber,
                cpf,
                active,
                toResponse(parkUser, active)
        );
    }

    private Record toResponse(ParkUser parkUser, Boolean active) {
        return switch (parkUser) {
            case Guard guard -> guardMapper.toGetResponse(guard, active);
            case Collaborator collaborator -> collaboratorMapper.toResponse(collaborator, active);
            case Visitor visitor -> visitorMapper.toResponse(visitor, active);
            default -> throw new NotFoundException("Can not found a user of this type");
        };
    }

    private Map<Long, Boolean> getUsersActive(List<? extends ParkUser> parkUsers) {
        if (parkUsers.isEmpty()) {
            return Map.of();
        }

        List<Long> userIds = parkUsers
                .stream()
                .map(ParkUser::getId)
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

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private Boolean getUserActive (UUID targetUuid) {
        CompletableFuture<Boolean> isUserActive = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new IsParkUserActiveEvent(isUserActive, targetUuid));
        return isUserActive.join();
    }
}
