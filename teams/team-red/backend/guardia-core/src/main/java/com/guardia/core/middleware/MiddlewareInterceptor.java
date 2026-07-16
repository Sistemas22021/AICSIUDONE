package com.guardia.core.middleware;

import com.guardia.core.security.SsoTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class MiddlewareInterceptor implements HandlerInterceptor {

    private final SsoTokenValidator ssoTokenValidator;

    public MiddlewareInterceptor(SsoTokenValidator ssoTokenValidator) {
        this.ssoTokenValidator = ssoTokenValidator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        AuthenticationHandler auth = new AuthenticationHandler(ssoTokenValidator);
        RequestLoggingHandler loggerHandler = new RequestLoggingHandler();
        auth.setNext(loggerHandler);

        if (requiereRolEspecifico(request)) {
            RoleValidationHandler roleValidator = new RoleValidationHandler();
            loggerHandler.setNext(roleValidator);
        }

        auth.handle(request, response);

        if (response.isCommitted() || response.getStatus() >= 400) {
            return false;
        }

        return true;
    }

    private boolean requiereRolEspecifico(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        return ("POST".equals(method) && uri.equals("/api/v1/expedientes"))
                || ("PUT".equals(method) && uri.matches("^/api/v1/expedientes/\\d+/sellar$"))
                || ("POST".equals(method) && uri.equals("/api/v1/evidencias"))
                || ("POST".equals(method) && uri.matches("^/api/v1/escenas/\\d+/liberar$"));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
    }
}