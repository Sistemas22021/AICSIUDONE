package com.guardia.core.security;

import java.util.Optional;

public interface SsoTokenValidator {
    Optional<String> validarYExtraerUsuario(String token);
}