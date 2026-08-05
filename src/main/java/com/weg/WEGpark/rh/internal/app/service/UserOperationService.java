package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.rh.DesactivateAndActivateUserEvent;
import com.weg.WEGpark.rh.FindParkUserEvent;
import com.weg.WEGpark.rh.GetParkUsersEvent;
import com.weg.WEGpark.rh.UserSearchResult;
import com.weg.WEGpark.rh.internal.app.mapper.UserOperationMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
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
        Pageable sourcePageable = createSourcePageable(pageable);

        CompletableFuture<Page<UserSearchResult>> eventResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new GetParkUsersEvent(eventResponse, filter, sourcePageable));
        Page<UserSearchResult> parkUsersResponse = eventResponse.join();
        Page<UserSearchResult> rhUsersResponse = rhService.listRhUsers(filter, sourcePageable);

        Set<UUID> foundUuids = new HashSet<>();
        List<Record> responseList = Stream.concat(
                        parkUsersResponse.getContent().stream(),
                        rhUsersResponse.getContent().stream()
                )
                .sorted(createComparator(sourcePageable.getSort()))
                .filter(user -> foundUuids.add(user.uuid()))
                .map(UserSearchResult::response)
                .toList();

        long totalElements = parkUsersResponse.getTotalElements() + rhUsersResponse.getTotalElements();

        return toPage(responseList, pageable, totalElements);
    }

    private Pageable createSourcePageable(Pageable pageable) {
        if (pageable.isUnpaged()) {
            return pageable;
        }

        long requestedWindow = pageable.getOffset() >= Integer.MAX_VALUE
                ? Integer.MAX_VALUE
                : pageable.getOffset() + pageable.getPageSize();
        int windowSize = (int) Math.min(requestedWindow, Integer.MAX_VALUE);
        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by("id");
        if (sort.getOrderFor("id") == null) {
            sort = sort.and(Sort.by("id"));
        }

        return PageRequest.of(0, windowSize, sort);
    }

    private Comparator<UserSearchResult> createComparator(Sort sort) {
        Sort effectiveSort = sort.isSorted() ? sort : Sort.by("id");
        Comparator<UserSearchResult> comparator = (first, second) -> 0;

        for (Sort.Order order : effectiveSort) {
            comparator = comparator.thenComparing(comparatorFor(order));
        }

        return comparator;
    }

    private Comparator<UserSearchResult> comparatorFor(Sort.Order order) {
        return switch (order.getProperty()) {
            case "id" -> compareBy(UserSearchResult::id, order);
            case "uuid" -> compareBy(UserSearchResult::uuid, order);
            case "email" -> compareBy(UserSearchResult::email, order);
            case "telephone" -> compareBy(UserSearchResult::telephone, order);
            case "name" -> compareBy(UserSearchResult::name, order);
            case "badgeNumber" -> compareBy(UserSearchResult::badgeNumber, order);
            case "cpf" -> compareBy(UserSearchResult::cpf, order);
            case "active" -> compareBy(UserSearchResult::active, order);
            default -> throw new IllegalArgumentException(
                    "Unsupported property for user sorting: " + order.getProperty()
            );
        };
    }

    private <T extends Comparable<? super T>> Comparator<UserSearchResult> compareBy(
            Function<UserSearchResult, T> valueExtractor,
            Sort.Order order
    ) {
        Comparator<T> valueComparator = Comparator.naturalOrder();
        if (order.isDescending()) {
            valueComparator = valueComparator.reversed();
        }

        boolean nullsFirst = switch (order.getNullHandling()) {
            case NULLS_FIRST -> true;
            case NULLS_LAST -> false;
            case NATIVE -> order.isDescending();
        };
        Comparator<T> nullableComparator = nullsFirst
                ? Comparator.nullsFirst(valueComparator)
                : Comparator.nullsLast(valueComparator);

        return Comparator.comparing(valueExtractor, nullableComparator);
    }

    private Page<Record> toPage(List<Record> content, Pageable pageable, long totalElements) {
        if (pageable.isUnpaged()) {
            return new PageImpl<>(content);
        }

        long offset = pageable.getOffset();
        if (offset >= content.size()) {
            return new PageImpl<>(List.of(), pageable, totalElements);
        }

        int fromIndex = (int) offset;
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), content.size());

        return new PageImpl<>(content.subList(fromIndex, toIndex), pageable, totalElements);
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
