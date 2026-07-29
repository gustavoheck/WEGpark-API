package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.app.exception.AccountEmailNotActiveException;
import com.weg.WEGpark.auth.internal.app.exception.InvalidLoginException;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.login.LoginRequestDTO;
import com.weg.WEGpark.auth.internal.dto.login.LoginResponseDTO;
import com.weg.WEGpark.auth.internal.dto.login.SelectAccountResponseDTO;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.TokenConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final AuthNotificationService authNotificationService;

    public List<SelectAccountResponseDTO> preLogin (String email) {
        List<User> users = userRepository.findByEmail(email);
        if (!users.isEmpty()) {
            return users.stream()
                    .map(user -> new SelectAccountResponseDTO(user.getRole().getRole()))
                    .toList();
        }
        throw new InvalidLoginException();
    }

    @Transactional
    public LoginResponseDTO login (LoginRequestDTO request) {
        User userLogin;
        if (request.role() != null) {
            userLogin = userRepository.findByEmailAndRole(request.email(), RolesType.valueOf(request.role()))
                    .orElseThrow(() -> new InvalidLoginException());
        } else {
            List<User> users = userRepository.findByEmail(request.email());
            if (users.isEmpty()) throw new InvalidLoginException();
            userLogin = users.getFirst();
        }
        if (userLogin.getEmailValidated() == true) {
            UsernamePasswordAuthenticationToken userAndPass =
                    new UsernamePasswordAuthenticationToken(userLogin.getId(), request.password());
            try {
                Authentication authentication = authenticationManager.authenticate(userAndPass);

                User userToken = (User) authentication.getPrincipal();

                String token = tokenConfig.generateToken(userToken);

                return new LoginResponseDTO(token);
            } catch (UsernameNotFoundException | BadCredentialsException e) {
                throw new InvalidLoginException();
            }
        }
        authNotificationService.sendAccountEmailValidation(userLogin.getEmail(), userLogin.getRole().getRole());
        throw new AccountEmailNotActiveException("Your email is not active");
    }
}
