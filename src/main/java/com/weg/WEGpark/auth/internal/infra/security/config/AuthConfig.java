package com.weg.WEGpark.auth.internal.infra.security.config;

import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthConfig implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String userId) throws UsernameNotFoundException {
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("Password or Email is invalid"));
    }
}
