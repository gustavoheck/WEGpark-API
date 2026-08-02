package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.GetParkUserNameEvent;
import com.weg.WEGpark.rh.GetRhUserNameEvent;
import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthUserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public String getUserName(User user) {
        RolesType role = user.getRole().getRole();

        if (role == RolesType.ROLE_ADMIN) {
            return "Admin";
        }

        CompletableFuture<String> eventResponse = new CompletableFuture<>();
        if (role == RolesType.ROLE_RH) {
            applicationEventPublisher.publishEvent(new GetRhUserNameEvent(eventResponse, user.getUuid()));
        } else {
            applicationEventPublisher.publishEvent(new GetParkUserNameEvent(eventResponse, user.getUuid()));
        }

        return eventResponse.join();
    }

    public void getActive (IsParkUserActiveEvent event) {
        Optional<User> optUser = userRepository.findByUuid(event.targetUserUuid());

        if (optUser.isPresent()) {
            User user = optUser.get();
            event.eventResponse().complete(user.getActive());
        }
        event.eventResponse().completeExceptionally(new NotFoundException("Any user was found by %s uuid".formatted(event.targetUserUuid())));
    }
}
