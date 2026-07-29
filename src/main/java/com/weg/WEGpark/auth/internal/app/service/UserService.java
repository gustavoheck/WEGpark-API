package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public void getActive (IsParkUserActiveEvent event) {
        Optional<User> optUser = userRepository.findByUuid(event.targetUserUuid());

        if (optUser.isPresent()) {
            User user = optUser.get();
            event.eventResponse().complete(user.getActive());
        }
        event.eventResponse().completeExceptionally(new NotFoundException("Any user was found by %s uuid".formatted(event.targetUserUuid())));
    }
}
