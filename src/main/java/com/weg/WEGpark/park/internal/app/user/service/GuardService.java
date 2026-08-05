package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.GuardRegisteredEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.GuardMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.infra.repository.GuardRepository;
import com.weg.WEGpark.rh.UpdateGuardEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GuardService {

    private final GuardMapper guardMapper;
    private final GuardRepository guardRepository;

    public void registerGuard (GuardRegisteredEvent event) {
        Guard guard = guardMapper.toEntity(event);
        guard.setUserType(ParkUserType.GUARD);

        guardRepository.save(guard);

        event.registerResponse().complete(guardMapper.toEventResponse(event));
    }

    public void updateGuard (UpdateGuardEvent event) {
        Guard guard = guardRepository.findByUuid(event.uuid())
                .orElseThrow(() -> new NotFoundException("Any user was found by %s uuid".formatted(event.uuid())));

        guardMapper.updateFromEvent(event, guard);

        guardRepository.save(guard);

        event.eventResponse().complete(guardMapper.toUpdatedEvent(guard));
    }
}
