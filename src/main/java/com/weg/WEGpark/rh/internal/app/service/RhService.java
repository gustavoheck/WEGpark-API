package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.DefaultRegisteredEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.rh.internal.app.mapper.RhMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.domain.model.Operation;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.dto.rh.*;
import com.weg.WEGpark.rh.internal.infra.repository.OperationRepository;
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
    private final OperationRepository operationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public RegisterRhResponseDTO registerRh (RegisterRhRequestDTO request, JWTUserData jwtUserData) {
        CompletableFuture<DefaultRegisteredEvent> eventResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(rhMapper.toRegisterEvent(request.defaults(), eventResponse));
        DefaultRegisteredEvent response = eventResponse.join();

        Rh rh = rhMapper.toEntity(request);
        rh.setId(response.id());
        rh.setUuid(response.uuid());
        rh.setEmail(response.email());

        System.out.println(response.email());
        System.out.println(response.id());
        System.out.println(response.uuid());

        rhRepository.save(rh);

        //Ele esta cadastrando um usuario igual duas vezes para rh, voce precisa passar pelos filtros para bloquear isso

//        Operation operation = new Operation(OperationType.CREATE, response.uuid());
//
////        Rh executorRh = rhRepository.findByUuid(jwtUserData.uuid())
////                .orElseThrow(() -> new NotFoundException("Any rh account was found by the logged uuid"));
////        operation.setRh(executorRh);
////        operationRepository.save(operation);

        return rhMapper.toRegisterResponse(rh);
    }

    @Transactional
    public UpdateRhResponseDTO updateRh (UpdateRhRequestDTO request, UUID uuid, JWTUserData jwtUserData) {
        Rh rh = rhRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Any rh account was found by %s uuid".formatted(uuid)));

        rhMapper.updateFromDTO(request, rh);

        rhRepository.save(rh);

        Rh executorRh = rhRepository.findByUuid(jwtUserData.uuid())
                .orElseThrow(() -> new NotFoundException("Any rh account was found by the logged uuid"));

        Operation operation = new Operation(OperationType.UPDATE, rh.getUuid());
        operation.setRh(executorRh);
        operationRepository.save(operation);

        return rhMapper.toUpdateResponse(rh);
    }

    public Page<GetRhResponseDTO> listRhUsers (FindUserFilter findUserFilter, Pageable pageable) {
        if (FilterUtil.checkMoreThanOneFilter(findUserFilter)) {
            if (FilterUtil.checkHaveFilter(findUserFilter)) {
                if (findUserFilter.active() != null) {
                    List<GetRhResponseDTO> responseList;
                    responseList = rhRepository
                            .findAll()
                            .stream()
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
}
