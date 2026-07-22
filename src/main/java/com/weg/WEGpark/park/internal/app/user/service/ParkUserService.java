package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParkUserService {
    private final ParkUserRepository parkUserRepository;

    public Boolean verifyParkUserToRegister (String email) {
        Boolean existsByEmail = parkUserRepository.existsByEmail(email);

        return existsByEmail;
    }
}
