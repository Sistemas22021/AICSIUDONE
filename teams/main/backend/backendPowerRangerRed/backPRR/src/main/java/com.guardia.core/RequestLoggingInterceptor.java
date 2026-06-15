package com.guardia.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String ATTR_START = "__request_start_nanos";
    private static final String ATTR_ERROR_DETAIL = "wife.logging.error-detail";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(ATTR_START, System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        Object startObj = request.getAttribute(ATTR_START);
        long durationMs = -1;
        if (startObj instanceof Long) {
            long start = (Long) startObj;
            durationMs = (System.nanoTime() - start) / 1_000_000;
        }

        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        LogResult result;
        if (status >= 200 && status < 300) {
            result = LogResult.EXITOSO;
        } else if (status == 422) {
            result = LogResult.ERROR_NEGOCIO;
        } else if (status == 404 || (status >= 400 && status < 422)) {
            result = LogResult.ERROR_FUNCIONAL;
        } else if (status >= 500) {
            result = LogResult.ERROR_INTERNO;
        } else {
            // fallback
            result = LogResult.ERROR_INTERNO;
        }

        String detail = null;
        Object attr = request.getAttribute(ATTR_ERROR_DETAIL);
        if (attr instanceof String) {
            detail = (String) attr;
        } else if (ex != null) {
            detail = ex.getMessage();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==============================\n");
        sb.append("[").append(method).append("] ").append(uri).append('\n');
        sb.append("Estado HTTP: ").append(status).append('\n');
        if (durationMs >= 0) sb.append("Duración: ").append(durationMs).append(" ms\n");
        sb.append("Resultado: ").append(result.name()).append('\n');
        if (detail != null && !detail.isBlank()) {
            sb.append("Detalle: ").append(detail).append('\n');
        }
        sb.append("==============================");

        // Log once per request
        logger.info(sb.toString());
    }
}
