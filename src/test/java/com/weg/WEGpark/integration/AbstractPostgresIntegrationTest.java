package com.weg.WEGpark.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public abstract class AbstractPostgresIntegrationTest {
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18")
            .withDatabaseName("wegpark_test")
            .withUsername("test")
            .withPassword("test");

    static {
        POSTGRES.start();
    }

    private static final KeyPair JWT_KEYS = createKeys();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> "2525");
        registry.add("security.variables.private-key", () -> pem("PRIVATE KEY", JWT_KEYS.getPrivate().getEncoded()));
        registry.add("security.variables.public-key", () -> pem("PUBLIC KEY", JWT_KEYS.getPublic().getEncoded()));
    }

    private static KeyPair createKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to generate test JWT keys", exception);
        }
    }

    private static String pem(String type, byte[] encoded) {
        return "-----BEGIN " + type + "-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(encoded)
                + "\n-----END " + type + "-----";
    }
}
