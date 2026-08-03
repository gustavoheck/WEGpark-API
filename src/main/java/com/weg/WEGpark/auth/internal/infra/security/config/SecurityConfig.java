package com.weg.WEGpark.auth.internal.infra.security.config;

import com.weg.WEGpark.auth.shared.enums.RolesType;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).hasAuthority(RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.POST,
                                "/auth",
                                "/auth/login",
                                "/auth/register/collaborator",
                                "/auth/register/visitor",
                                "/auth/reset-password/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/validate-email/*").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/admin").denyAll()
                        .requestMatchers("/rh/**")
                                .hasAnyAuthority(RolesType.ROLE_RH.name(), RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/notification")
                                .hasAnyAuthority(
                                        RolesType.ROLE_PARK.name(),
                                        RolesType.ROLE_GUARD.name(),
                                        RolesType.ROLE_RH.name(),
                                        RolesType.ROLE_ADMIN.name()
                                )
                        .requestMatchers(HttpMethod.DELETE, "/notification/*")
                                .hasAnyAuthority(
                                        RolesType.ROLE_PARK.name(),
                                        RolesType.ROLE_GUARD.name(),
                                        RolesType.ROLE_RH.name(),
                                        RolesType.ROLE_ADMIN.name()
                                )
                        .requestMatchers(HttpMethod.GET, "/park/profile")
                                .hasAnyAuthority(RolesType.ROLE_PARK.name(), RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH,
                                "/park/profile/collaborator",
                                "/park/profile/visitor"
                        ).hasAnyAuthority(RolesType.ROLE_PARK.name(), RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/vehicle")
                                .hasAnyAuthority(RolesType.ROLE_PARK.name(), RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/vehicle/associate/**")
                                .hasAnyAuthority(RolesType.ROLE_PARK.name(), RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/vehicle/me")
                                .hasAnyAuthority(RolesType.ROLE_PARK.name(), RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/vehicle")
                                .hasAnyAuthority(RolesType.ROLE_GUARD.name(), RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/vehicle/*")
                                .hasAnyAuthority(
                                        RolesType.ROLE_PARK.name(),
                                        RolesType.ROLE_GUARD.name(),
                                        RolesType.ROLE_ADMIN.name()
                                )
                        .requestMatchers(HttpMethod.GET, "/occurrence")
                                .hasAnyAuthority(
                                        RolesType.ROLE_RH.name(),
                                        RolesType.ROLE_GUARD.name(),
                                        RolesType.ROLE_ADMIN.name()
                                )
                        .requestMatchers(HttpMethod.GET, "/occurrence/me")
                                .hasAnyAuthority(RolesType.ROLE_PARK.name(), RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.POST,
                                "/occurrence/warning",
                                "/occurrence/traffic-accident",
                                "/occurrence/illegal-parking"
                        ).hasAnyAuthority(RolesType.ROLE_GUARD.name(), RolesType.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/occurrence/**")
                                .hasAnyAuthority(RolesType.ROLE_GUARD.name(), RolesType.ROLE_ADMIN.name())
                        .anyRequest().hasAuthority(RolesType.ROLE_ADMIN.name())
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
