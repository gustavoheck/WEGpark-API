package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.rh.GetParkUsersEvent;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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


    public void findParkUsers(GetParkUsersEvent event, Pageable pageable) {
        if (FilterUtil.checkHaveFilter(event.findUserFilter())) {
            if (FilterUtil.checkMoreThanOneFilter(event.findUserFilter())) {

                Page<Record> findByName = findParkUserName(event, pageable);
                Page<Record> findByCpf = findVisitorCpf(event, pageable);
                Page<Record> findByBadgeNumber = findColaboratorBadgeNumber(event, pageable);
                Page<Record> findByActive = findAllActiveDesactiveUsers(event, pageable);

                List<Record> responseList = Stream.of(
                                findByName,
                                findByCpf,
                                findByBadgeNumber,
                                findByActive)
                        .flatMap(page -> page.getContent().stream())
                        .toList();

                long totalElements = findByCpf.getTotalElements() + findByName.getTotalElements() + findByBadgeNumber.getTotalElements();

                event.eventResponse().complete(new PageImpl<>(responseList, pageable, totalElements));
            }
            event.eventResponse().completeExceptionally(new MoreThenOneFilterException("You can not use more than one filter to search for users"));
        }
        event.eventResponse().complete(findAllUsers(event, pageable));
    }

    private Page<Record> findAllUsers(GetParkUsersEvent event, Pageable pageable) {
        Page<ParkUser> parkUsers = parkUserRepository.findAll(pageable);
        List<Record> responseList;
        if (!parkUsers.isEmpty()) {
            responseList = parkUsers.map(parkUser -> {
                Boolean isActive = getUserActive(parkUser.getUuid());
                switch (parkUser) {
                    case Guard guard -> {
                        return guardMapper.toGetResponse(guard, isActive);
                    }
                    case Collaborator collaborator -> {
                        return collaboratorMapper.toResponse(collaborator, isActive);
                    }
                    case Visitor visitor -> {
                        return visitorMapper.toResponse(visitor, isActive);
                    }
                    default -> {
                        event.eventResponse().completeExceptionally(new NotFoundException("Can not found a user of this type"));
                    }
                }
                return null;
            }).filter(Objects::nonNull).toList();
            return new PageImpl<>(responseList, pageable, responseList.size());
        }
        return Page.empty();
    }

    private Page<Record> findAllActiveDesactiveUsers(GetParkUsersEvent event, Pageable pageable) {
        Page<ParkUser> parkUsers = parkUserRepository.findAll(pageable);
        List<Record> responseList;
        Boolean activeFilter = event.findUserFilter().active();
        if (activeFilter != null) {
            if (!parkUsers.isEmpty()) {
                responseList = parkUsers.stream().map(parkUser -> {
                    Boolean isActive = getUserActive(parkUser.getUuid());
                    if (isActive == activeFilter) {
                        switch (parkUser) {
                            case Guard guard -> {
                                return guardMapper.toGetResponse(guard, isActive);
                            }
                            case Collaborator collaborator -> {
                                return collaboratorMapper.toResponse(collaborator, isActive);
                            }
                            case Visitor visitor -> {
                                return visitorMapper.toResponse(visitor, isActive);
                            }
                            default -> {
                                event.eventResponse().completeExceptionally(new NotFoundException("Can not found a user of this type"));
                            }
                        }
                    }
                    return null;
                }).filter(Objects::nonNull).toList();
                return new PageImpl<>(responseList, pageable, responseList.size());
            }
        }
        return Page.empty();
    }

    private Page<Record> findParkUserName(GetParkUsersEvent event, Pageable pageable) {
        String name = event.findUserFilter().name();
        if (name != null) {
            Page<ParkUser> parkUsers = parkUserRepository.findByNameLike(name, pageable);
            if (!parkUsers.isEmpty()) {
                List<Record> responseList = parkUsers.map(parkUser -> {
                        Boolean isActive = getUserActive(parkUser.getUuid());
                        switch (parkUser) {
                            case Guard guard -> {
                                return guardMapper.toGetResponse(guard, isActive);
                            }
                            case Collaborator collaborator -> {
                                return collaboratorMapper.toResponse(collaborator, isActive);
                            }
                            case Visitor visitor -> {
                                return visitorMapper.toResponse(visitor, isActive);
                            }
                            default -> {
                                event.eventResponse().completeExceptionally(new NotFoundException("Can not found a user of this type"));
                            }
                        }
                    return null;
                }).filter(Objects::nonNull).toList();
                return new PageImpl<>(responseList, pageable, responseList.size());
            }
        }
        return Page.empty();
    }

    private Page<Record> findColaboratorBadgeNumber(GetParkUsersEvent event, Pageable pageable) {
        String badgeNumber = event.findUserFilter().badgeNumber();
        if (badgeNumber != null) {
            Page<Collaborator> collaborators = collaboratorRepository.findByBadgeNumber(badgeNumber, pageable);
            if (!collaborators.isEmpty()) {
                Page<Record> responseList = collaborators.map(collaborator -> {
                    Boolean isActive = getUserActive(collaborator.getUuid());
                    switch (collaborator) {
                        case Guard guard -> {
                            return guardMapper.toGetResponse(guard, isActive);
                        }
                        default -> {
                            return collaboratorMapper.toResponse(collaborator, isActive);
                        }
                    }
                });
                return responseList;
            }
            return Page.empty();
        }
        return Page.empty();
    }

    private Page<Record> findVisitorCpf(GetParkUsersEvent event, Pageable pageable) {
        String cpf = event.findUserFilter().cpf();
        if (cpf != null) {
            Page<Visitor> visitorResponse = visitorRepository.findVisitorByCpf(cpf, pageable);
            Page<Record> responsePage = visitorResponse
                    .map(visitor -> visitorMapper.toResponse(visitor, getUserActive(visitor.getUuid())));
        }
        return Page.empty();
    }

    private Boolean getUserActive (UUID targetUuid) {
        CompletableFuture<Boolean> isUserActive = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new IsParkUserActiveEvent(isUserActive, targetUuid));
        return isUserActive.join();
    }
}
