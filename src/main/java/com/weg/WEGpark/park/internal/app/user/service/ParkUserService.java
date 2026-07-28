package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.park.internal.app.shared.util.FilterUtil;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public Boolean verifyParkUserToRegister(String email) {
        Boolean existsByEmail = parkUserRepository.existsByEmail(email);

        return existsByEmail;
    }


    public Page<Object> findParkUser(FindUserFilter findUserFilter, Pageable pageable) {
        Page<Response> responsePage;
        if (FilterUtil.checkHaveFilter(findUserFilter)) {
            if (FilterUtil.checkMoreThanOneFilter(findUserFilter)) {

                Page<Object> findByName = findParkUserName(findUserFilter.name(), pageable);
                Page<Object> findByCpf = findVisitorCpf(findUserFilter.cpf(), pageable);
                Page<Object> findByBadgeNumber = findColaboratorBadgeNumber(findUserFilter.badgeNumber(), pageable);

                List<Object> responseList = Stream.of(
                                findByName,
                                findByCpf,
                                findByBadgeNumber)
                        .flatMap(page -> page.getContent().stream())
                        .toList();

                long totalElementos = findByCpf.getTotalElements() + findByName.getTotalElements() + findByBadgeNumber.getTotalElements();

                return new PageImpl<>(responseList, pageable, totalElementos);
            }
            throw new MoreThenOneFilterException("You can not use more than one filter to search for users");
        }
        return findAllUsers(pageable);
    }

    public Page<Object> findAllUsers(Pageable pageable) {
        Page<ParkUser> parkUsers = parkUserRepository.findAll(pageable);
        Page<Object> responseList;
        if (parkUsers.isEmpty()) {
            responseList = parkUsers.map(parkUser ->
                    switch (parkUser) {
                        case Guard guard -> {
                            yield guardMapper.toGetResponse(guard);
                        }
                        case Collaborator collaborator -> {
                            yield collaboratorMapper.toResponse(collaborator);
                        }
                        case Visitor visitor -> {
                            yield visitorMapper.toResponse(visitor);
                        }
                        default -> {
                            throw new NotFoundException("Can not found a user of this type");
                        }
                    }
            );
        }
        return Page.empty();
    }

    public Page<Object> findParkUserName(String name, Pageable pageable) {
        if (name != null) {
            Page<ParkUser> parkUsers = parkUserRepository.findByNameLike(name, pageable);
            if (parkUsers.isEmpty()) {
                Page<Object> responseList = parkUsers.map(parkUser ->
                        switch (parkUser) {
                            case Guard guard -> {
                                yield guardMapper.toGetResponse(guard);
                            }
                            case Collaborator collaborator -> {
                                yield collaboratorMapper.toResponse(collaborator);
                            }
                            case Visitor visitor -> {
                                yield visitorMapper.toResponse(visitor);
                            }
                            default -> {
                                throw new NotFoundException("Can not found a user of this type");
                            }
                        }
                );
                return responseList;
            }
        }
        return Page.empty();
    }

    public Page<Object> findColaboratorBadgeNumber(String badgeNumber, Pageable pageable) {
        if (badgeNumber != null) {
            Page<Collaborator> collaborators = collaboratorRepository.findByBadgeNumber(badgeNumber, pageable);
            if (!collaborators.isEmpty()) {
                Page<Object> responseList = collaborators.map(collaborator -> {
                    switch (collaborator) {
                        case Guard guard -> {
                            return guardMapper.toGetResponse(guard);
                        }
                        default -> {
                            return collaboratorMapper.toResponse(collaborator);
                        }
                    }
                });
            }
            return Page.empty();
        }
        return Page.empty();
    }

    public Page<Object> findVisitorCpf(String cpf, Pageable pageable) {
        if (cpf != null) {
            Page<Visitor> visitorResponse = visitorRepository.findVisitorByCpf(cpf, pageable);
            Page<Object> responsePage = visitorResponse
                    .map(visitor -> visitorMapper.toResponse(visitor));
        }
        return Page.empty();
    }
}
