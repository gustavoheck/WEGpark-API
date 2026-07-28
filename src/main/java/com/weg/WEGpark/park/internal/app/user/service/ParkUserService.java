package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import com.weg.WEGpark.shared.util.FilterUtil;
import com.weg.WEGpark.park.internal.app.user.mapper.CollaboratorMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.GuardMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VisitorMapper;
import com.weg.WEGpark.park.internal.app.vehicle.exception.MoreThenOneFilterException;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.internal.infra.repository.CollaboratorRepository;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VisitorRepository;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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


    public Page<Record> findParkUser(FindUserFilter findUserFilter, Pageable pageable) {
        if (FilterUtil.checkHaveFilter(findUserFilter)) {
            if (FilterUtil.checkMoreThanOneFilter(findUserFilter)) {

                Page<Record> findByName = findParkUserName(findUserFilter.name(), pageable);
                Page<Record> findByCpf = findVisitorCpf(findUserFilter.cpf(), pageable);
                Page<Record> findByBadgeNumber = findColaboratorBadgeNumber(findUserFilter.badgeNumber(), pageable);
                Page<Record> findByActive = findAllActiveDesactiveUsers(findUserFilter.active(), pageable);

                List<Record> responseList = Stream.of(
                                findByName,
                                findByCpf,
                                findByBadgeNumber,
                                findByActive)
                        .flatMap(page -> page.getContent().stream())
                        .toList();

                long totalElements = findByCpf.getTotalElements() + findByName.getTotalElements() + findByBadgeNumber.getTotalElements();

                return new PageImpl<>(responseList, pageable, totalElements);
            }
            throw new MoreThenOneFilterException("You can not use more than one filter to search for users");
        }
        return findAllUsers(pageable);
    }

    public Page<Record> findAllUsers(Pageable pageable) {
        Page<ParkUser> parkUsers = parkUserRepository.findAll(pageable);
        Page<Record> responseList;
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
                        throw new NotFoundException("Can not found a user of this type");
                    }
                }
            });
            return responseList;
        }
        return Page.empty();
    }

    public Page<Record> findAllActiveDesactiveUsers(Boolean activeFilter, Pageable pageable) {
        Page<ParkUser> parkUsers = parkUserRepository.findAll(pageable);
        List<Record> responseList;
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
                                throw new NotFoundException("Can not found a user of this type");
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

    public Page<Record> findParkUserName(String name, Pageable pageable) {
        if (name != null) {
            Page<ParkUser> parkUsers = parkUserRepository.findByNameLike(name, pageable);
            if (!parkUsers.isEmpty()) {
                Page<Record> responseList = parkUsers.map(parkUser -> {
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
                                throw new NotFoundException("Can not found a user of this type");
                            }
                        }
                    }
                );
                return responseList;
            }
        }
        return Page.empty();
    }

    public Page<Record> findColaboratorBadgeNumber(String badgeNumber, Pageable pageable) {
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
            }
            return Page.empty();
        }
        return Page.empty();
    }

    public Page<Record> findVisitorCpf(String cpf, Pageable pageable) {
        if (cpf != null) {
            Page<Visitor> visitorResponse = visitorRepository.findVisitorByCpf(cpf, pageable);
            Page<Record> responsePage = visitorResponse
                    .map(visitor -> visitorMapper.toResponse(visitor, getUserActive(visitor.getUuid())));
        }
        return Page.empty();
    }

    public Boolean getUserActive (UUID targetUuid) {
        CompletableFuture<Boolean> isUserActive = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new IsParkUserActiveEvent(isUserActive, targetUuid));
        return isUserActive.join();
    }
}
