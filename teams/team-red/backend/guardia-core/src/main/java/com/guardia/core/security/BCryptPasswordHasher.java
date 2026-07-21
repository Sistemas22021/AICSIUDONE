package com.guardia.core.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return false;
        }
        if (hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}