package com.guardia.core.security;

import com.guardia.core.middleware.AuthenticationHandler;
import com.guardia.core.repository.UsuarioRepository;
import com.guardia.core.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final UsuarioRepository usuarioRepository;

    public Optional<String> username() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return Optional.empty();
        Object value = attrs.getRequest()
                .getAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME);
        return (value instanceof String username && !username.isBlank())
                ? Optional.of(username)
                : Optional.empty();
    }

    public Optional<Usuario> usuario() {
        return username().flatMap(usuarioRepository::findByUsername);
    }
}