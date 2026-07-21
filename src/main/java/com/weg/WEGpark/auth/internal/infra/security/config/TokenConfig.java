package com.weg.WEGpark.auth.internal.infra.security.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.security.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenConfig {

    private final SecurityVariables securityVariables;

    private final Algorithm algorithm = Algorithm
            .RSA256(publicKeyFormatter(securityVariables.publicKey()),
                    privateKeyFormatter(securityVariables.privateKey()));

    public String generateToken(User user) {
        return JWT.create()
                .withClaim("uuid", user.getUuid().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plus(user.getRole().getRole().getTokenExpirationTime(), ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            DecodedJWT decode = JWT.require(algorithm)
                    .build().verify(token);

            return Optional.of(JWTUserData.builder()
                    .uuid(UUID.fromString(decode.getClaim("userUuid").asString()))
                    .email(decode.getSubject())
                    .roles(decode.getClaim("roles").asList(String.class))
                    .build()
            );
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException();
        }
    }

    private RSAPrivateKey privateKeyFormatter(String privateKey) {
        String formattedKey = privateKey.replace("\\n", "\n");
        ByteArrayInputStream privateKeyStream = new ByteArrayInputStream(formattedKey.getBytes(StandardCharsets.UTF_8));
        return RsaKeyConverters.pkcs8().convert(privateKeyStream);
    }

    private RSAPublicKey publicKeyFormatter(String publicKey) {
        String formattedKey = publicKey.replace("\\n", "\n");
        ByteArrayInputStream publicKeyStream = new ByteArrayInputStream(formattedKey.getBytes(StandardCharsets.UTF_8));
        return RsaKeyConverters.x509().convert(publicKeyStream);
    }
}
