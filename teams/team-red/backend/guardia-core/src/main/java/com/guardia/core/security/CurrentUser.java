package com.guardia.core.security;

import com.guardia.core.middleware.AuthenticationHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public class CurrentUser {

    public Optional<String> username() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return Optional.empty();
        }
        HttpServletRequest request = attrs.getRequest();
        Object value = request.getAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME);
        return (value instanceof String username && !username.isBlank())
                ? Optional.of(username)
                : Optional.empty();
    }
}