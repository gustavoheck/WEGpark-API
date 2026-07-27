package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.GuardRegisteredEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.GuardMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GuardService {

    private final GuardMapper guardMapper;
    private final ParkUserRepository parkUserRepository;

    public void registerGuard (GuardRegisteredEvent event) {
        Guard guard = guardMapper.toEntity(event);
        guard.setUserType(ParkUserType.GUARD);

        parkUserRepository.save(guard);

        event.registerResponse().complete(guardMapper.toEventResponse(event));
    }
}
