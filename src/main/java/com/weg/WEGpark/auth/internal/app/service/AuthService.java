package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.app.exception.InvalidLoginException;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.login.LoginRequestDTO;
import com.weg.WEGpark.auth.internal.dto.login.LoginResponseDTO;
import com.weg.WEGpark.auth.internal.infra.security.config.TokenConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    @Transactional
    public LoginResponseDTO login (LoginRequestDTO request) {
        UsernamePasswordAuthenticationToken userAndPass =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        try {
            Authentication authentication = authenticationManager.authenticate(userAndPass);

            User user = (User) authentication.getPrincipal();

            String token = tokenConfig.generateToken(user);

            return new LoginResponseDTO(token);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            throw new InvalidLoginException();
        }
    }
}
